package com.sc.event.subscriber;

import com.sc.event.subscriber.utils.HashCodeUtil;
import com.sc.event.subscriber.utils.ObjectUtil;

import java.lang.reflect.Method;

/**
 * created by 2018/10/16 下午6:33
 * <p>
 * 保存订阅的方法信息
 *
 * @author Ryan
 */
class SubscribeMethod {

    private Object master;
    private Method method;
    private int threadMode;
    private Class<?> eventType;
    private final int mHash;

    SubscribeMethod(Object master, Method method, Class<?> eventType, int threadMode) {
        this.master = master;
        this.method = method;
        this.eventType = eventType;
        this.threadMode = threadMode;
        mHash = HashCodeUtil.hashCode(master, method, eventType, threadMode);
    }

    Object getMaster() {
        return master;
    }

    Method getMethod() {
        return method;
    }

    int getThreadMode() {
        return threadMode;
    }

    Class<?> getEventType() {
        return eventType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SubscribeMethod)) {
            return false;
        }
        SubscribeMethod other = (SubscribeMethod) obj;
        return mHash == other.hashCode()
                && ObjectUtil.equal(master, other.master)
                && ObjectUtil.equal(method, other.method)
                && threadMode == other.threadMode
                && ObjectUtil.equal(eventType, other.eventType);
    }

    @Override
    public int hashCode() {
        return mHash;
    }
}
