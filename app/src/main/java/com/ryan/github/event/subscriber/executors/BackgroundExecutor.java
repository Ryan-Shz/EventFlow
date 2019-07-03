package com.ryan.github.event.subscriber.executors;

import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * created by 2018/10/16 下午7:57
 *
 * @author Ryan
 */
public class BackgroundExecutor implements Executor {

    private static final int CORE_THREADS = Runtime.getRuntime().availableProcessors() << 1;
    private static final int KEEP_LIVE_TIME = 60;
    private final ThreadPoolExecutor mExecutor;

    public BackgroundExecutor() {
        BlockingQueue<Runnable> queue = new SynchronousQueue<>();
        mExecutor = new ThreadPoolExecutor(CORE_THREADS, CORE_THREADS, KEEP_LIVE_TIME, TimeUnit.SECONDS, queue);
        mExecutor.allowCoreThreadTimeOut(true);
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mExecutor.execute(command);
    }
}
