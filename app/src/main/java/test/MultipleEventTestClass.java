package test;

import android.util.Log;

import com.ryan.github.event.subscriber.EventFlow;
import com.ryan.github.event.subscriber.ThreadMode;
import com.ryan.github.event.subscriber.annotation.Subscribe;

import static test.Constants.TAG;

/**
 * Created by Ryan
 * at 2019/7/3
 */
public class MultipleEventTestClass {

    void register() {
        EventFlow subscriber = EventFlow.getInstance();
        subscriber.register(this);
    }

    void unRegister() {
        EventFlow subscriber = EventFlow.getInstance();
        subscriber.unRegister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 2)
    public void onEvent(TestEvent event) {
        Log.v(TAG, getClass().getSimpleName() + " receive event! " + event + ", event id: " + event.id);
    }
}
