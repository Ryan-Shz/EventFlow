package com.sc.event.subscriber;

import com.sc.event.subscriber.utils.HashCodeUtil;
import com.sc.event.subscriber.utils.ObjectUtil;

/**
 * created by 2018/10/16 下午7:13
 *
 * @author Ryan
 */
class EventSubscriber {

    private Object subscribeObject;
    private Class<?> eventType;
    private final int mHash;

    EventSubscriber(Object subscribeObject, Class<?> eventType) {
        this.subscribeObject = subscribeObject;
        this.eventType = eventType;
        mHash = HashCodeUtil.hashCode(subscribeObject, eventType);
    }

    public Object getSubscribeObject() {
        return subscribeObject;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EventSubscriber)) {
            return false;
        }
        EventSubscriber other = (EventSubscriber) obj;
        return mHash == other.hashCode()
                && ObjectUtil.equal(subscribeObject, other.subscribeObject)
                && ObjectUtil.equal(eventType, other.eventType);
    }

    @Override
    public int hashCode() {
        return mHash;
    }

}
