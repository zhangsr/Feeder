package me.zsr.feeder;

import android.app.Application;

/**
 * @description:
 * @author: Saul
 * @date: 15-5-13
 * @version: 1.0
 */
public class App extends Application {
    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }
}
