package com.humanheima.videoplayerdemo;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * Created by Mr.Robot on 2018/1/23.
 * https://github.com/Siomt
 */

public class MyPhoneCallListener extends PhoneStateListener {

    private static final String TAG = "MyPhoneCallListener";
    protected CallListener listener;
         /**
         * 返回电话状态
         *
         * CALL_STATE_IDLE 无任何状态时
         * CALL_STATE_OFFHOOK 接起电话时
         * CALL_STATE_RINGING 电话进来时
         */
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:// 电话挂断
                Log.d(TAG ,"电话挂断...");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK: //电话通话的状态
                Log.d(TAG ,"正在通话...");
                listener.onCallRinging();
                break;
            case TelephonyManager.CALL_STATE_RINGING: //电话响铃的状态
                Log.d(TAG ,"电话响铃");
                break;
        }
        super.onCallStateChanged(state, incomingNumber);
    }
    //写个回调
    public void setCallListener(CallListener callListener){
        this.listener = callListener;
    }
    //回调接口
    public interface CallListener{
        void onCallRinging();
    }
}