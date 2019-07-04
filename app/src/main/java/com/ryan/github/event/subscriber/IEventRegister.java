package com.ryan.github.event.subscriber;

import java.util.List;

/**
 * created by 2018/10/16 下午5:18
 * <p>
 * 事件订阅接口
 *
 * @author Ryan
 */
public interface IEventRegister {

    /**
     * 订阅事件
     *
     * @param subscriber 被订阅对象
     */
    void register(Object subscriber);

    /**
     * 取消订阅
     *
     * @param subscriber 取消订阅对象
     */
    void unRegister(Object subscriber);

    /**
     * 该对象是否被订阅
     *
     * @param subscriber 订阅对象
     * @return true已订阅/false未订阅
     */
    boolean isRegister(Object subscriber);

    List<SubscribeMethod> getSubscribeMethods(Class<?> eventType);

    List<SubscribeMethod> getStickySubscribeMethods(Class<?> eventType);

}
