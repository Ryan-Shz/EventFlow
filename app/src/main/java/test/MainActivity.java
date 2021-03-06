package test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ryan.github.event.subscriber.EventFlow;
import com.ryan.github.event.subscriber.R;
import com.ryan.github.event.subscriber.ThreadMode;
import com.ryan.github.event.subscriber.annotation.Subscribe;

public class MainActivity extends AppCompatActivity {

    private MultipleEventTestClass mMultipleEventTestClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMultipleEventTestClass = new MultipleEventTestClass();
        Button postButton = findViewById(R.id.post_btn);
        Button postSingleButton = findViewById(R.id.post_single_btn);
        Button sticky_test_btn = findViewById(R.id.sticky_test_btn);
        Button removeStickyBtn = findViewById(R.id.remove_sticky_btn);
        registerEventFlow();
        EventFlow.getInstance().postSticky(new TestEvent());
        postSingleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postSingleEvent();
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postEvent();
            }
        });
        sticky_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), StickyTestActivity.class));
            }
        });
        removeStickyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventFlow.getInstance().removeStickyEvent(TestEvent.class);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterEventFlow();
    }

    public void postEvent() {
        for (int i = 0; i < 10; i++) {
            postInNewThread(i);
        }
    }

    public void postSingleEvent() {
        TestEvent event = new TestEvent();
        EventFlow.getInstance().post(event);
    }

    public void postInNewThread(final int i) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int j = 0; j < 10; j++) {
                    TestEvent event = new TestEvent();
                    event.id = i * 10 + j;
                    EventFlow.getInstance().post(event);
                }
            }
        }.start();
    }

    public void registerEventFlow() {
        EventFlow subscriber = EventFlow.getInstance();
        subscriber.register(this);
        mMultipleEventTestClass.register();
    }

    public void unRegisterEventFlow() {
        EventFlow subscriber = EventFlow.getInstance();
        subscriber.unRegister(this);
        mMultipleEventTestClass.unRegister();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void onEvent(TestEvent event) {
        Log.v(Constants.TAG, getClass().getSimpleName() + " receive event! " + event + ", event id: " + event.id);
        Toast.makeText(this, "receive event!", Toast.LENGTH_LONG).show();
    }
}
