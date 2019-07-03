package com.ryan.github.event.subscriber;

import com.ryan.github.event.subscriber.executors.BackgroundExecutor;
import com.ryan.github.event.subscriber.executors.MainExecutor;
import com.ryan.github.event.subscriber.executors.PosterExecutor;

import java.util.concurrent.Executor;

/**
 * created by 2018/10/16 下午8:05
 * <p>
 * 根据{@link ThreadMode} 提供指定的线程执行
 *
 * @author Ryan
 */
public class RealExecutorsService implements ExecutorsService {

    private BackgroundExecutor backgroundExecutor;
    private MainExecutor mainExecutor;
    private PosterExecutor posterExecutor;

    RealExecutorsService() {
        backgroundExecutor = new BackgroundExecutor();
        mainExecutor = new MainExecutor();
        posterExecutor = new PosterExecutor();
    }

    @Override
    public Executor getExecutor(int threadMode) {
        switch (threadMode) {
            case ThreadMode.BACKGROUND:
                return backgroundExecutor;
            case ThreadMode.MAIN:
                return mainExecutor;
            case ThreadMode.POSTER:
                return posterExecutor;
            default:
                throw new RuntimeException("方法指定的threadMode: " + threadMode + " 无效!");
        }
    }
}
