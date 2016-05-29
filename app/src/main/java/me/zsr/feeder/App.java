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

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import me.zsr.feeder.dao.DaoMaster;
import me.zsr.feeder.dao.DaoSession;
import me.zsr.feeder.dao.FeedAccount;
import me.zsr.feeder.dao.FeedAccountDao;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.util.LogUtil;
import me.zsr.feeder.util.NetworkUtil;

/**
 * @description: Global init, context.
 * @author: Match
 * @date: 15-5-13
 */
public class App extends Application {
    public static final long SOURCE_ID_ALL = -1;
    public static final String KEY_BUNDLE_ITEM_ID = "item_id";
    private static final String SP_ADD_DEFAULT = "add_default";
    private static final String ACCOUNT_NAME_DEFAULT = "Local";
    private static final String SP_CURRENT_ACCOUNT = "current_account";
    public static final String SP_REFRESH_DEFAULT = "refresh_default";
    private static final String DB_NAME = "feed_db";
    private static App sInstance;
    private static SharedPreferences sSharePreferences;
    private static DaoSession sDaoSession;
    private static FeedAccount sCurrentFeedAccount;

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

    public static FeedAccount getCurrentAccount() {
        if (sCurrentFeedAccount == null) {
            String accountName = getSharePreferences().getString(SP_CURRENT_ACCOUNT, ACCOUNT_NAME_DEFAULT);
            List<FeedAccount> feedAccountList = getDaoSession().getFeedAccountDao().queryBuilder()
                    .where(FeedAccountDao.Properties.Name.eq(accountName)).list();
            if (feedAccountList.size() == 1) {
                sCurrentFeedAccount = feedAccountList.get(0);
            } else {
                LogUtil.e("Somethings wrong with Account init !!");
                // TODO: 5/28/16 try to restart and init again
            }
        }
        return sCurrentFeedAccount;
    }

    public static void updateCurrentAccount(FeedAccount feedAccount) {
        if (feedAccount != null && feedAccount.getId().equals(getCurrentAccount().getId())) {
            SharedPreferences.Editor editor = getSharePreferences().edit();
            editor.putString(SP_CURRENT_ACCOUNT, feedAccount.getName());
            editor.apply();
            sCurrentFeedAccount = feedAccount;
            // TODO: 5/28/16 notify others ?
        }
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
        // TODO: 5/28/16 not bad to hold SharePreference ?
        if (sSharePreferences == null) {
            sSharePreferences = PreferenceManager.getDefaultSharedPreferences(getInstance());
        }
        return sSharePreferences;
    }

    public void initDB() {
        if (NetworkUtil.isNetworkEnabled(getInstance())
                && !getSharePreferences().getBoolean(SP_ADD_DEFAULT, false)) {
            FeedAccount feedAccount = new FeedAccount(null, ACCOUNT_NAME_DEFAULT, null);
            getDaoSession().getFeedAccountDao().insertOrReplace(feedAccount);
            getDaoSession().getFeedSourceDao().insertOrReplace(new FeedSource(
                    null,
                    "知乎每日精选",
                    "http://www.zhihu.com/rss",
                    null,
                    "http://www.zhihu.com",
                    "http://img.wdjimg.com/mms/icon/v1/f/a6/c713050654880cef2d1b579448893a6f_256_256.png",
                    "一个真实的网络问答社区，帮助你寻找答案，分享知识",
                    null,
                    feedAccount.getId()
            ));
            getDaoSession().getFeedSourceDao().insertOrReplace(new FeedSource(
                    null,
                    "36氪",
                    "http://www.36kr.com/feed",
                    null,
                    "http://36kr.com",
                    "http://krplus-cdn.b0.upaiyun.com/common-module/common-header/images/logo.png",
                    "36氪，让创业更简单",
                    null,
                    feedAccount.getId()
            ));

            SharedPreferences.Editor editor = getSharePreferences().edit();
            editor.putBoolean(SP_ADD_DEFAULT, true);
            editor.apply();
        }
    }

    private void initShareSDK() {
        ShareSDK.initSDK(this);
    }
}
