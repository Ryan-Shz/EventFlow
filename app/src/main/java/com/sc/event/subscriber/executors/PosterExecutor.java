package com.sc.event.subscriber.executors;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * created by 2018/10/16 下午8:28
 *
 * @author Ryan
 */
public class PosterExecutor implements Executor {

    @Override
    public void execute(@NonNull Runnable command) {
        command.run();
    }
}
