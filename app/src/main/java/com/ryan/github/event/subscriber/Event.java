package com.ryan.github.event.subscriber;

import android.support.v4.util.Pools;

/**
 * created by 2018/10/16 下午7:34
 * <p>
 * 事件按一定顺序处理，方便之后扩展事件按优先级执行
 * 这边采用单链表实现，旧的事件放前面，新的事件发后面
 * 之后使用链表根据排序
 *
 * @author Ryan
 */
class Event {

    private static Pools.Pool<Event> sPool = new Pools.SimplePool<>(5);

    static Event obtain(Object postEvent) {
        Event event = sPool.acquire();
        if (event != null) {
            event.postEvent = postEvent;
            return event;
        }
        return new Event(postEvent);
    }

    void recycle() {
        sPool.release(this);
    }

    Event next;

    Object postEvent;

    private Event(Object event) {
        this.postEvent = event;
    }
}
