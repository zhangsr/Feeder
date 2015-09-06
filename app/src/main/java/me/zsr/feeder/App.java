package me.zsr.feeder;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import me.zsr.feeder.dao.DaoMaster;
import me.zsr.feeder.dao.DaoSession;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.service.BackgroundRefreshService;
import me.zsr.feeder.util.NetworkUtil;

/**
 * @description: Global init, context.
 * @author: Match
 * @date: 15-5-13
 */
public class App extends Application {
    public static final String KEY_BUNDLE_SOURCE_ID = "source_id";
    public static final String KEY_BUNDLE_ITEM_ID = "item_id";
    private static final String SP_ADD_DEFAULT = "add_default";
    private static final String WX_APP_ID = "wxf0b102ba70e9fae2";
    private static final String DB_NAME = "feed_db";
    private static final String AVOS_APP_ID = "ms2lsbjilfbqjeb5fitysvm0lkt38nnw2bvwe60sy7j5g50t";
    private static final String AVOS_CLIENT_KEY = "84gf4pv73s99zme304ks1e5f5qwdpls1exgg5cx7c2rah0u4";
    private static App sInstance;
    private static IWXAPI sIWXApi;
    private static SharedPreferences sSharePreferences;
    private static DaoSession sDaoSession;
    public Mode mCurrentMode = Mode.UNREAD;
    public enum Mode {
        STAR, UNREAD, ALL
    }

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        initUniversalImageLoader();
        initBackgroundRefreshService();
        initLeanCloud();
        initWeiXin();
        initDB();
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

    private void initBackgroundRefreshService() {
        startService(new Intent(this, BackgroundRefreshService.class));
    }

    private void initLeanCloud() {
        AVOSCloud.initialize(this, AVOS_APP_ID, AVOS_CLIENT_KEY);
        AVAnalytics.enableCrashReport(this, true);
    }

    private void initWeiXin() {
        sIWXApi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        sIWXApi.registerApp(WX_APP_ID);
    }

    public static IWXAPI getWXAPI() {
        return sIWXApi;
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
                    "http://statics.dnspod.cn/proxy_favicon/_/favicon?domain=" + "www.zhihu.com",
                    "一个真实的网络问答社区，帮助你寻找答案，分享知识"));
            getDaoSession().getFeedSourceDao().insertOrReplace(new FeedSource(
                    null,
                    "36氪",
                    "http://www.36kr.com/feed",
                    null,
                    "http://36kr.com",
                    "http://statics.dnspod.cn/proxy_favicon/_/favicon?domain=" + "36kr.com",
                    "36氪，让创业更简单"));

            SharedPreferences.Editor editor = getSharePreferences().edit();
            editor.putBoolean(SP_ADD_DEFAULT, true);
            editor.apply();
        }
    }
}
