package com.sc.event.subscriber;

/**
 * created by 2018/10/16 下午8:07
 *
 * @author Ryan
 */
public interface ThreadMode {

    // 在提交事件的线程执行
    int POSTER = 0;
    // 在线程池中执行
    int BACKGROUND = 1;
    // 在主线程中执行
    int MAIN = 2;
    // ... 扩展

}
