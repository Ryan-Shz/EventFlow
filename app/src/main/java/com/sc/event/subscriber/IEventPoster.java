package com.sc.event.subscriber;

/**
 * created by 2018/10/16 下午5:43
 * <p>
 * 事件的发送接口，实现post(Event)方法来提交一个事件
 *
 * @author Ryan
 */
public interface IEventPoster {

    /**
     * 提交事件
     *
     * @param event 事件对象
     * @return 是否正常提交
     */
    boolean post(Object event);

}
