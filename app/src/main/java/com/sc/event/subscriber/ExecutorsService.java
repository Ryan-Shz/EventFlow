package com.sc.event.subscriber;

import java.util.concurrent.Executor;

/**
 * created by 2018/10/16 下午8:06
 * <p>
 * 根据不同的ThreadMode提供不同的线程执行器
 *
 * @author Ryan
 */
public interface ExecutorsService {

    Executor getExecutor(int threadMode);

}
