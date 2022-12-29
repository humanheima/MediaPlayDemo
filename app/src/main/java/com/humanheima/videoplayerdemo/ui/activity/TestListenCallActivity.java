package com.humanheima.videoplayerdemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import butterknife.ButterKnife;
import com.humanheima.videoplayerdemo.MyPhoneCallListener;
import com.humanheima.videoplayerdemo.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by p_dmweidu on 2022/8/3
 * Desc: 测试监听来电，时间变化，等等
 *
 * https://blog.csdn.net/wuqingyidongren/article/details/51899201
 */
public class TestListenCallActivity extends AppCompatActivity {

    private final String TAG = "TestListenCallActivity";

    DateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");


    public static void launch(Context context) {
        Intent starter = new Intent(context, TestListenCallActivity.class);
        context.startActivity(starter);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                //updateTime();//每一分钟更新时间
                Log.i(TAG, "onReceive: ACTION_TIME_TICK");
            } else if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
                Log.i(TAG, "onReceive: ACTION_TIME_CHANGED");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_listen_call);
        ButterKnife.bind(this);
        telephony();
        Log.e(TAG, "onCreate: now elapsedRealtime = " + format.format(SystemClock.elapsedRealtime()));
        Log.e(TAG, "onCreate: now uptimeMillis = " + format.format(SystemClock.uptimeMillis()));

        findViewById(R.id.btn_test_SystemClock).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemClock.setCurrentTimeMillis(System.currentTimeMillis());
                Log.e(TAG, "onCreate: now elapsedRealtime = " + format.format(SystemClock.elapsedRealtime()));
                Log.e(TAG, "onCreate: now uptimeMillis = " + format.format(SystemClock.uptimeMillis()));
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);//一分钟，一次。
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        registerReceiver(broadcastReceiver, filter);
        //广播的注册，其中Intent.ACTION_TIME_CHANGED代表时间设置变化的时候会发出该广播
    }

    private void telephony() {

        //获得相应的系统服务
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (tm != null) {
            try {
                MyPhoneCallListener myPhoneCallListener = new MyPhoneCallListener();
                myPhoneCallListener.setCallListener(new MyPhoneCallListener.CallListener() {
                    @Override
                    public void onCallRinging() {
                        //回调，做你想做的，我是关闭当前界面
                        finish();
                    }
                });
                // 注册来电监听
                tm.listen(myPhoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);
            } catch (Exception e) {
                // 异常捕捉
                Log.i(TAG, "telephony: error " + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}
