package me.zsr.feeder;

import android.app.Application;
import android.content.Context;

import com.android.volley.toolbox.ImageLoader;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import me.zsr.feeder.dao.DaoMaster;
import me.zsr.feeder.dao.DaoSession;
import me.zsr.feeder.util.LogUtil;

/**
 * @description: Global init, context.
 * @author: Match
 * @date: 15-5-13
 */
public class App extends Application {
    public static final String KEY_BUNDLE_SOURCE_ID = "source_id";
    public static final String KEY_BUNDLE_ITEM_ID = "item_id";
    private static final String DB_NAME = "feed_db";
    private static App sInstance;
    private static DaoSession sDaoSession;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public static DaoSession getDaoSession() {
        if (sDaoSession == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(getInstance(), DB_NAME, null);
            sDaoSession = new DaoMaster(helper.getWritableDatabase()).newSession();
        }
        return sDaoSession;
    }
}
