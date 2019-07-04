package com.ryan.github.event.subscriber;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * created by 2018/10/16 下午5:44
 * <p>
 * 事件发送实现类
 * 1. 从当前线程ThreadLocal中获取待发送的事件状态
 * 2. 根据ThreadMode选择不同的线程执行
 * <p>
 * 使用ThreadLocal保存使用此线程发送的事件，不同线程间的事件不互相干扰，
 * EventQueue不需要添加同步操作，避免同步带来的性能开销
 *
 * @author Ryan
 */
public class RealEventPoster implements IEventPoster {

    private EventFlow mEventFlow;
    private ExecutorsService mService;
    private ThreadLocal<PendingEventState> mThreadLocal;
    private final Map<Class<?>, List<Object>> mStickyEventPool;

    RealEventPoster(EventFlow eventFlow) {
        mEventFlow = eventFlow;
        mService = new RealExecutorsService();
        mThreadLocal = new ThreadLocal<>();
        mStickyEventPool = new ArrayMap<>();
    }

    @Override
    public boolean post(Object event) {
        return post(event, false);
    }

    private boolean post(Object event, boolean isSticky) {
        PendingEventState state = mThreadLocal.get();
        if (state == null) {
            state = new PendingEventState();
            mThreadLocal.set(state);
        }
        Event eventWrapper = Event.obtain(event);
        state.enqueue(eventWrapper);
        boolean isPosting = state.isPosting();
        if (!isPosting) {
            state.setPosting(true);
            while ((eventWrapper = state.next()) != null) {
                postSingleEvent(eventWrapper, isSticky);
            }
            state.setPosting(false);
        }
        return true;
    }

    @Override
    public void postSticky(Object event) {
        Class eventClass = event.getClass();
        synchronized (mStickyEventPool) {
            List<Object> eventsList = mStickyEventPool.get(eventClass);
            if (eventsList == null) {
                eventsList = new ArrayList<>();
                mStickyEventPool.put(eventClass, eventsList);
            }
            eventsList.add(event);
        }
        post(event, true);
    }

    @Override
    public void stickyToAll() {
        synchronized (mStickyEventPool) {
            for (Map.Entry<Class<?>, List<Object>> entry : mStickyEventPool.entrySet()) {
                List<Object> eventsList = entry.getValue();
                for (Object event : eventsList) {
                    post(event, true);
                }
            }
        }
    }

    @Override
    public void removeStickyEvent(Object event) {
        Class eventClass = event.getClass();
        synchronized (mStickyEventPool) {
            List<Object> eventsList = mStickyEventPool.get(eventClass);
            if (eventsList != null && !eventsList.isEmpty()) {
                eventsList.remove(event);
            }
        }
    }

    @Override
    public void removeAllStickyEvents() {
        synchronized (mStickyEventPool) {
            mStickyEventPool.clear();
        }
    }

    @Override
    public void removeStickyEvent(Class<?> eventType) {
        synchronized (mStickyEventPool) {
            mStickyEventPool.remove(eventType);
        }
    }

    private void postSingleEvent(Event eventWrapper, boolean isSticky) {
        Object event = eventWrapper.postEvent;
        eventWrapper.recycle();
        Class<?> eventType = event.getClass();
        List<SubscribeMethod> methods = isSticky ? mEventFlow.getStickySubscribeMethods(eventType)
                : mEventFlow.getSubscribeMethods(eventType);
        if (methods == null || methods.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.v(EventFlow.TAG, "event: " + event + ", 没有被订阅");
            }
            return;
        }
        for (SubscribeMethod method : methods) {
            postInSpecialExecutor(method, event);
        }
    }

    private void postInSpecialExecutor(final SubscribeMethod method, final Object event) {
        // 获取要线程执行器
        int threadMode = method.getThreadMode();
        Executor executor = mService.getExecutor(threadMode);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Object subscriber = method.getMaster();
                try {
                    method.getMethod().invoke(subscriber, event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
