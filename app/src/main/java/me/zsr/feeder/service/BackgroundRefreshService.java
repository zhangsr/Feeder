package me.zsr.feeder.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.lang.ref.WeakReference;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.data.FeedNetwork;
import me.zsr.feeder.util.CommonEvent;
import me.zsr.feeder.util.NetworkUtil;

public class BackgroundRefreshService extends Service {
    private static final int TIME_REFRESH_DELAY = 5 * 60 * 1000;
    private static final int MSG_REFRESH = 0;
    private Handler mHandler = new MyHandler(this);

    public BackgroundRefreshService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        mHandler.sendEmptyMessageDelayed(MSG_REFRESH, TIME_REFRESH_DELAY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private static class MyHandler extends Handler {
        private WeakReference<BackgroundRefreshService> mService;

        MyHandler(BackgroundRefreshService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            BackgroundRefreshService service = mService.get();
            if (service != null) {
                switch (msg.what) {
                    case MSG_REFRESH:
                        if (NetworkUtil.isWifiEnabled(App.getInstance())) {
                            FeedNetwork.getInstance().refreshAll();
                        }
                        break;
                    default:
                }
            }
        }
    }

    public void onEventMainThread(CommonEvent commonEvent) {
        switch (commonEvent) {
            case FEED_DB_UPDATED:
                if (!mHandler.hasMessages(MSG_REFRESH)) {
                    mHandler.sendEmptyMessageDelayed(MSG_REFRESH, TIME_REFRESH_DELAY);
                }
                break;
            default:
        }
    }
}
