package me.zsr.feeder;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.sharesdk.framework.ShareSDK;
import me.zsr.feeder.dao.DaoMaster;
import me.zsr.feeder.dao.DaoSession;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.util.NetworkUtil;

/**
 * @description: Global init, context.
 * @author: Match
 * @date: 15-5-13
 */
public class App extends Application {
    public static final long SOURCE_ID_ALL = -1;
    public static final String KEY_BUNDLE_ITEM_TITLE = "item_title";
    private static final String SP_ADD_DEFAULT = "add_default";
    public static final String SP_REFRESH_DEFAULT = "refresh_default";
    private static final String DB_NAME = "feed_db";
    private static App sInstance;
    private static SharedPreferences sSharePreferences;
    private static DaoSession sDaoSession;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        initUniversalImageLoader();
        initLeanCloud();
        initDB();
        initShareSDK();
    }

    public static DaoSession getDaoSession() {
        if (sDaoSession == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(getInstance(), DB_NAME, null);
            sDaoSession = new DaoMaster(helper.getWritableDatabase()).newSession();
        }
        return sDaoSession;
    }

    private void initUniversalImageLoader() {
        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(this)
                        .diskCacheSize(50 * 1024 * 1024)
                        .threadPriority(Thread.MAX_PRIORITY)
                        .denyCacheImageMultipleSizesInMemory()
                        .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                        .tasksProcessingOrder(QueueProcessingType.FIFO)
                        .build();
        ImageLoader.getInstance().init(config);
    }

    private void initLeanCloud() {
        // TODO: 11/30/15 Verify
        if (BuildConfig.DEBUG) {
            AVAnalytics.setAnalyticsEnabled(false);
        }
        AVAnalytics.setAppChannel(BuildConfig.LEANCLOUD_CHANNEL);
        AVOSCloud.initialize(this, BuildConfig.AVOS_APP_ID, BuildConfig.AVOS_CLIENT_KEY);
        AVAnalytics.enableCrashReport(this, true);
    }

    public static SharedPreferences getSharePreferences() {
        if (sSharePreferences == null) {
            sSharePreferences = PreferenceManager.getDefaultSharedPreferences(getInstance());
        }
        return sSharePreferences;
    }

    public void initDB() {
        if (NetworkUtil.isNetworkEnabled(getInstance())
                && !getSharePreferences().getBoolean(SP_ADD_DEFAULT, false)) {
            getDaoSession().getFeedSourceDao().insertOrReplace(new FeedSource(
                    null,
                    "知乎每日精选",
                    "http://www.zhihu.com/rss",
                    null,
                    "http://www.zhihu.com",
                    "http://img.wdjimg.com/mms/icon/v1/f/a6/c713050654880cef2d1b579448893a6f_256_256.png",
                    "一个真实的网络问答社区，帮助你寻找答案，分享知识"));
            getDaoSession().getFeedSourceDao().insertOrReplace(new FeedSource(
                    null,
                    "36氪",
                    "http://www.36kr.com/feed",
                    null,
                    "http://36kr.com",
                    "http://krplus-cdn.b0.upaiyun.com/common-module/common-header/images/logo.png",
                    "36氪，让创业更简单"));

            SharedPreferences.Editor editor = getSharePreferences().edit();
            editor.putBoolean(SP_ADD_DEFAULT, true);
            editor.apply();
        }
    }

    private void initShareSDK() {
        ShareSDK.initSDK(this);
    }
}
