package com.humanheima.videoplayerdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.humanheima.videoplayerdemo.ui.base.BaseActivity;
import com.humanheima.videoplayerdemo.ui.base.BaseVideoActivity;


/**
 * Created by chenchao on 16/10/19.
 * cc@cchao.org
 */
public class NetChangeReceiver extends BroadcastReceiver {

    private BaseActivity activity;

    public void setActivity(BaseVideoActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            activity.netChanged();
        }
    }
}
