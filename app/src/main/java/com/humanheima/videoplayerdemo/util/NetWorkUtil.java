package com.humanheima.videoplayerdemo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.humanheima.videoplayerdemo.App;


/**
 * Created by chenchao on 16/9/29.
 * cc@cchao.org
 */
public class NetWorkUtil {

    private NetWorkUtil() {}

    /**
     * 获取活动网络信息
     *
     * @param context 上下文
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * 判断网络是否连接
     *
     * @return
     */
    public static boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo(App.getInstance());
        if (null != info) {
            return info.isAvailable();
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getActiveNetworkInfo(App.getInstance());
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}
