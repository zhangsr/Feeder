package me.zsr.feeder;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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
}
