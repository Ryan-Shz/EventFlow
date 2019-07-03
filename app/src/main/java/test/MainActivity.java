package test;

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
        Button registerButton = findViewById(R.id.register_btn);
        Button postButton = findViewById(R.id.post_btn);
        Button unRegisterButton = findViewById(R.id.unregister_btn);
        Button postSingleButton = findViewById(R.id.post_single_btn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerEventFlow();
            }
        });
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
        unRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unRegisterEventFlow();
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
