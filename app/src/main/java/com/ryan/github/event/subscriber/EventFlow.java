package com.ryan.github.event.subscriber;

import java.util.List;

/**
 * created by 2018/10/16 下午5:09
 * <p>
 * 事件流外观类，同一封装接口
 *
 * @author Ryan
 */
public class EventFlow implements IEventRegister, IEventPoster {

    public static final String TAG = EventFlow.class.getSimpleName();
    private IEventRegister mRegister;
    private IEventPoster mPoster;

    private EventFlow() {
        mRegister = new RealEventRegister();
        mPoster = new RealEventPoster(this);
    }

    public static EventFlow getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final EventFlow INSTANCE = new EventFlow();
    }

    @Override
    public boolean post(Object event) {
        return mPoster.post(event);
    }

    @Override
    public void postSticky(Object event) {
        mPoster.postSticky(event);
    }

    @Override
    public void stickyToAll() {
        mPoster.stickyToAll();
    }

    @Override
    public void removeStickyEvent(Object event) {
        mPoster.removeStickyEvent(event);
    }

    @Override
    public void removeAllStickyEvents() {
        mPoster.removeAllStickyEvents();
    }

    @Override
    public void removeStickyEvent(Class<?> eventType) {
        mPoster.removeStickyEvent(eventType);
    }

    @Override
    public void register(Object subscriber) {
        mRegister.register(subscriber);
        stickyToAll();
    }

    @Override
    public void unRegister(Object subscriber) {
        mRegister.unRegister(subscriber);
    }

    @Override
    public boolean isRegister(Object subscriber) {
        return mRegister.isRegister(subscriber);
    }

    @Override
    public List<SubscribeMethod> getSubscribeMethods(Class<?> eventType) {
        return mRegister.getSubscribeMethods(eventType);
    }

    @Override
    public List<SubscribeMethod> getStickySubscribeMethods(Class<?> eventType) {
        return mRegister.getStickySubscribeMethods(eventType);
    }

}
