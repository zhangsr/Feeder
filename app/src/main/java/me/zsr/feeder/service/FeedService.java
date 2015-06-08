package me.zsr.feeder.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class FeedService extends Service {
    private IBinder mBinder = new FeedBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class FeedBinder extends Binder {
        public FeedService getService() {
            return FeedService.this;
        }
    }
}
