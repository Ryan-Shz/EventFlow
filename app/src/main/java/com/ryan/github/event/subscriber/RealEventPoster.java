package com.ryan.github.event.subscriber;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * created by 2018/10/16 下午5:44
 * <p>
 * 事件发送实现类
 * 1. 从当前线程ThreadLocal中获取待发送的事件状态
 * 2. 根据ThreadMode选择不同的线程执行
 * <p>
 * 使用ThreadLocal保存使用此线程发送的事件，不同线程间的事件不互相干扰，
 * EventQueue不需要添加同步操作，避免同步带来的性能开销
 *
 * @author Ryan
 */
public class RealEventPoster implements IEventPoster {

    private EventFlow mEventFlow;
    private ExecutorsService mService;
    private ThreadLocal<PendingEventState> mThreadLocal;

    RealEventPoster(EventFlow eventFlow) {
        mEventFlow = eventFlow;
        mService = new RealExecutorsService();
        mThreadLocal = new ThreadLocal<>();
    }

    @Override
    public boolean post(Object event) {
        PendingEventState state = mThreadLocal.get();
        if (state == null) {
            state = new PendingEventState();
            mThreadLocal.set(state);
        }
        Event eventWrapper = Event.obtain(event);
        state.enqueue(eventWrapper);
        boolean isPosting = state.isPosting();
        if (!isPosting) {
            state.setPosting(true);
            while ((eventWrapper = state.next()) != null) {
                postSingleEvent(eventWrapper);
            }
            state.setPosting(false);
        }
        return true;
    }

    private void postSingleEvent(Event eventWrapper) {
        Object event = eventWrapper.postEvent;
        eventWrapper.recycle();
        Class<?> eventType = event.getClass();
        List<SubscribeMethod> methods = mEventFlow.getSubscribeMethods(eventType);
        if (methods == null || methods.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.v(EventFlow.TAG, "event: " + event + ", 没有被订阅");
            }
            return;
        }
        for (SubscribeMethod method : methods) {
            postInSpecialExecutor(method, event);
        }
    }

    private void postInSpecialExecutor(final SubscribeMethod method, final Object event) {
        // 获取要线程执行器
        int threadMode = method.getThreadMode();
        Executor executor = mService.getExecutor(threadMode);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Object subscriber = method.getMaster();
                try {
                    method.getMethod().invoke(subscriber, event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
