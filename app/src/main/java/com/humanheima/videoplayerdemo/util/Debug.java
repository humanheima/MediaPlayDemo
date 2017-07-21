package com.humanheima.videoplayerdemo.util;

import android.util.Log;

/**
 * Created by chenchao on 16/9/27.
 * cc@cchao.org
 */
public class Debug {

    public static void d(String TAG, String content) {
        if (content.length() > 4000) {
            for (int i = 0; i < content.length(); i += 4000) {
                if (i + 4000 < content.length()) {
                    Log.d(TAG, content.substring(i, i + 4000));
                } else {
                    Log.d(TAG, content.substring(i, content.length()));
                }
            }
        } else {
            Log.d(TAG, content);
        }
    }
}
