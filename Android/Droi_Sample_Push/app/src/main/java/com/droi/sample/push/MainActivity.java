package com.droi.sample.push;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.droi.sdk.core.TaskDispatcher;
import com.droi.sdk.push.DroiMessageHandler;
import com.droi.sdk.push.DroiPush;

import butterknife.BindView;
import butterknife.ButterKnife;

// For Weixin needed. The requested activity must named with wxapi.WXEnteryActivity
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind Views
        ButterKnife.bind(this);

        // Config toolbar
        setSupportActionBar(toolBar);

        // Navigate main fragment
        FragmentNavigator.naviMain(this, true);

        // Show custom message with SnackBar
        DroiPush.setMessageHandler(new DroiMessageHandler() {
            @Override
            public void onHandleCustomMessage(Context context, final String message) {
                TaskDispatcher.getDispatcher(TaskDispatcher.MainThreadName).enqueueTask(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getResources().getString(R.string.custom_message);
                        Snackbar.make(findViewById(R.id.root_view), msg + message, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
