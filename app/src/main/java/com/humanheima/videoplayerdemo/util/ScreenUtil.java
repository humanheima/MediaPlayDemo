package com.humanheima.videoplayerdemo.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by chenchao on 16/9/29.
 * cc@cchao.org
 */
public class ScreenUtil {

    public static class Data {
        public int px;
        public float dp;

        public Data(int px, float density) {
            this.px = px;
            this.dp = px / density;
        }
    }

    /**
     * 获取屏幕
     */
    public static Display getDisplay(Context context) {
        Display d = ((Activity) context).getWindowManager().getDefaultDisplay();
        return d;
    }

    public static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int spToPx(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 屏幕宽度
     */
    public static Data width(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        getDisplay(context).getMetrics(dm);
        return new Data(dm.widthPixels, dm.density);
    }

    /**
     * 屏幕高度
     */
    public static Data height(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        getDisplay(context).getMetrics(dm);
        return new Data(dm.heightPixels, dm.density);
    }
}
