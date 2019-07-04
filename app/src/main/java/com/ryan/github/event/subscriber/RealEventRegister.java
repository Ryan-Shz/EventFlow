package com.ryan.github.event.subscriber;

import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.ArraySet;

import com.ryan.github.event.subscriber.annotation.Subscribe;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * created by 2018/10/16 下午5:19
 * <p>
 * 事件注册
 *
 * @author Ryan
 */
public class RealEventRegister implements IEventRegister {

    // 保存目前订阅的对象，用于快速判断是否已订阅
    private Map<Class<?>, EventSubscriber> mSubscribers;
    // 保存<事件类型-订阅方法>映射
    private Map<Class<?>, List<SubscribeMethod>> mTypeAndEvents;
    // 保存对象和订阅方法，避免重复反射
    private Map<Class<?>, Set<SubscribeMethod>> mSubscriberMethods;
    private Map<Class<?>, List<SubscribeMethod>> mStickyMethods;

    RealEventRegister() {
        mTypeAndEvents = new ArrayMap<>();
        mSubscriberMethods = new ArrayMap<>();
        mSubscribers = new ArrayMap<>();
        mStickyMethods = new ArrayMap<>();
    }

    @Override
    public void register(Object subscriber) {
        // 限制register只能在主线程中执行，避免需要进行线程同步
        checkThread();
        // 不允许重复订阅
        if (isRegister(subscriber)) {
            throw new IllegalStateException("对象：" + subscriber.toString() + " 已订阅!");
        }
        // 执行订阅
        registerEventSubscriber(subscriber);
    }

    @Override
    public void unRegister(Object subscriber) {
        // 限制register只能在主线程中执行，避免需要进行线程同步
        checkThread();
        Class<?> subscriberClass = subscriber.getClass();
        if (mSubscribers.remove(subscriberClass) == null) {
            throw new IllegalStateException("没有订阅对象: " + subscriber.toString());
        }
        // 1. 先获取要取消订阅的对象中的所有订阅方法
        Set<SubscribeMethod> subscribeMethods = mSubscriberMethods.get(subscriberClass);
        if (subscribeMethods == null) {
            return;
        }
        // 2. 将该对象所有订阅方法从订阅方法集合中移除
        for (SubscribeMethod subscribeMethod : subscribeMethods) {
            Class<?> eventType = subscribeMethod.getEventType();
            List<SubscribeMethod> methods = mTypeAndEvents.get(eventType);
            methods.remove(subscribeMethod);
        }
    }

    @Override
    public synchronized boolean isRegister(Object subscriber) {
        Class<?> subscriberClass = subscriber.getClass();
        EventSubscriber innerSubscriber = mSubscribers.get(subscriberClass);
        return innerSubscriber != null;
    }

    @Override
    public List<SubscribeMethod> getSubscribeMethods(Class<?> eventType) {
        List<SubscribeMethod> subscribeMethods = mTypeAndEvents.get(eventType);
        if (subscribeMethods != null) {
            return Collections.unmodifiableList(subscribeMethods);
        }
        return null;
    }

    @Override
    public List<SubscribeMethod> getStickySubscribeMethods(Class<?> eventType) {
        List<SubscribeMethod> subscribeMethods = mStickyMethods.get(eventType);
        if (subscribeMethods != null) {
            return Collections.unmodifiableList(subscribeMethods);
        }
        return null;
    }

    private void checkThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Can not register() a subscriber from a non-main thread.");
        }
    }

    private Set<SubscribeMethod> findSubscriberMethods(Class<?> subscriberClass) {
        return mSubscriberMethods.get(subscriberClass);
    }

    private void registerEventSubscriber(Object subscriber) {
        Class<?> subscriberClass = subscriber.getClass();
        Set<SubscribeMethod> subscribeMethods = findSubscriberMethods(subscriberClass);
        if (subscribeMethods != null) {
            // 上次反射过，不再反射
            // 将上次反射的信息重新注册到<事件类型-事件方法>的映射列表里
            for (SubscribeMethod method : subscribeMethods) {
                Class<?> eventType = method.getEventType();
                List<SubscribeMethod> cachedMethods = getEventTypeMethodsSet(eventType);
                cachedMethods.add(method);
                EventSubscriber eventSubscriber = new EventSubscriber(subscriber, subscriberClass);
                mSubscribers.put(subscriberClass, eventSubscriber);
            }
            return;
        }
        // 没有反射缓存信息，执行反射
        Method[] methods = subscriberClass.getDeclaredMethods();
        subscribeMethods = new ArraySet<>();
        mSubscriberMethods.put(subscriberClass, subscribeMethods);
        for (Method method : methods) {
            // 该方法没有添加Subscribe注解，不是要订阅的方法，略过
            Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
            if (subscribeAnnotation == null) {
                continue;
            }
            // 判断订阅方法的合法性，必须是public、void
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && method.getReturnType().equals(Void.TYPE)) {
                // 判断方法参数数量，不能同时订阅多个事件
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    // 方法验证通过，执行订阅
                    // 1. 获取参数类型、线程模式
                    Class<?> eventType = parameterTypes[0];
                    int threadMode = subscribeAnnotation.threadMode();
                    int priority = subscribeAnnotation.priority();
                    boolean isSticky = subscribeAnnotation.sticky();
                    // 2. 保存到<事件类型-事件方法>集合中
                    SubscribeMethod subscribeMethod = new SubscribeMethod(subscriber, method, eventType, threadMode, priority, isSticky);
                    if (isSticky) {
                        List<SubscribeMethod> cachedMethods = getStickyMethodList(eventType);
                        int insertIndex = getPriorityIndex(cachedMethods, subscribeMethod);
                        cachedMethods.add(insertIndex, subscribeMethod);
                    } else {
                        List<SubscribeMethod> cachedMethods = getEventTypeMethodsSet(eventType);
                        int insertIndex = getPriorityIndex(cachedMethods, subscribeMethod);
                        cachedMethods.add(insertIndex, subscribeMethod);
                    }
                    // 3. 缓存订阅类的订阅方法信息，反注册时不会被清空，下次注册时不需要再次反射避免性能消耗
                    subscribeMethods.add(subscribeMethod);
                    // 4. 保存订阅者，用来快速判断是否已经订阅
                    EventSubscriber eventSubscriber = new EventSubscriber(subscriber, subscriberClass);
                    mSubscribers.put(subscriberClass, eventSubscriber);
                } else {
                    throw new RuntimeException("订阅方法参数长度不能超过1! method: " + method);
                }
            } else {
                // 方法验证失败，抛出异常
                throw new RuntimeException("订阅方法修饰符必须是public，并且返回值为void!");
            }
        }
    }

    private List<SubscribeMethod> getEventTypeMethodsSet(Class<?> eventType) {
        List<SubscribeMethod> cachedMethods = mTypeAndEvents.get(eventType);
        if (cachedMethods == null) {
            cachedMethods = new ArrayList<>();
            mTypeAndEvents.put(eventType, cachedMethods);
        }
        return cachedMethods;
    }

    public List<SubscribeMethod> getStickyMethodList(Class<?> eventType) {
        List<SubscribeMethod> cachedMethods = mStickyMethods.get(eventType);
        if (cachedMethods == null) {
            cachedMethods = new ArrayList<>();
            mStickyMethods.put(eventType, cachedMethods);
        }
        return cachedMethods;
    }

    private int getPriorityIndex(List<SubscribeMethod> cachedMethods, SubscribeMethod insertMethod) {
        int priority = insertMethod.getPriority();
        int insertIndex = 0;
        for (int i = 0; i < cachedMethods.size(); i++) {
            SubscribeMethod curr = cachedMethods.get(i);
            if (curr.getPriority() >= priority) {
                insertIndex++;
            } else {
                break;
            }
        }
        return insertIndex;
    }

}
