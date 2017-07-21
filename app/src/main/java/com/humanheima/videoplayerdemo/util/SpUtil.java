package com.humanheima.videoplayerdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.humanheima.videoplayerdemo.App;

/**
 * Created by dmw on 2016/9/12.
 */
public class SpUtil {

    private static SpUtil spUtil;
    private static SharedPreferences hmSpref;
    private static final String SP_NAME = "shbag_spref";
    public static final String ALLOW_MOBLE = "allow_mobile";//允许2G/3G/4G/播放视频

    private static SharedPreferences.Editor editor;

    private SpUtil() {
        hmSpref = App.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = hmSpref.edit();
    }

    public static SpUtil getInstance() {
        if (spUtil == null) {
            synchronized (SpUtil.class) {
                if (spUtil == null) {
                    spUtil = new SpUtil();
                }
            }
        }
        return spUtil;
    }

    public boolean getAllowMoblie() {
        return hmSpref.getBoolean(ALLOW_MOBLE, false);
    }

}
