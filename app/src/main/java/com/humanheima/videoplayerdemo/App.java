package com.humanheima.videoplayerdemo;

import android.app.Application;

/**
 * Created by chenchao on 16/9/27.
 * cc@cchao.org
 */
public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

}
