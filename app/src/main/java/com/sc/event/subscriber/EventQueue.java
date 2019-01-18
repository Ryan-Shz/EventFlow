package com.sc.event.subscriber;

/**
 * created by 2018/10/16 下午7:33
 * <p>
 * 事件池，由于每个线程只保存自己发送的事件，所以不需要做线程同步操作
 * 使用链表实现，可以方便的扩展按事件的优先级排序
 *
 * @author Ryan
 */
class EventQueue {

    private Event head;
    private Event tail;

    Event next() {
        if (head == null) {
            return null;
        }
        Event next;
        next = head;
        head = head.next;
        next.next = null;
        return next;
    }

    void enqueue(Event event) {
        if (head == null) {
            head = event;
            return;
        }
        if (tail != null) {
            tail.next = event;
            tail = event;
            return;
        }
        Event temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = event;
        tail = event;
    }

}
