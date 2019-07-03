package com.ryan.github.event.subscriber;

/**
 * created by 2018/10/16 下午8:37
 * <p>
 * 由每个线程各自持有，保存在ThreadLocal中
 *
 * @author Ryan
 */
public class PendingEventState {

    private EventQueue mQueue;
    private boolean isPosting;

    PendingEventState() {
        mQueue = new EventQueue();
    }

    void enqueue(Event event) {
        mQueue.enqueue(event);
    }

    Event next() {
        return mQueue.next();
    }

    public EventQueue getQueue() {
        return mQueue;
    }

    public boolean isPosting() {
        return isPosting;
    }

    public void setPosting(boolean posting) {
        isPosting = posting;
    }
}
