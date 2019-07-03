package com.ryan.github.event.subscriber.executors;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * created by 2018/10/16 下午7:55
 *
 * @author Ryan
 */
public class MainExecutor implements Executor {

    private Handler mHandler;

    public MainExecutor() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mHandler.post(command);
    }
}
