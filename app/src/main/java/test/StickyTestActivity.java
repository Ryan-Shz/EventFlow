package test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.ryan.github.event.subscriber.EventFlow;
import com.ryan.github.event.subscriber.ThreadMode;
import com.ryan.github.event.subscriber.annotation.Subscribe;

/**
 * Created by Ryan
 * at 2019/7/3
 */
public class StickyTestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventFlow.getInstance().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(TestEvent event) {
        Log.v(Constants.TAG, getClass().getSimpleName() + " receive event! " + event + ", event id: " + event.id);
        Toast.makeText(this, "receive event!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventFlow.getInstance().unRegister(this);
    }
}
