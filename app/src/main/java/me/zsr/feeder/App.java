package me.zsr.feeder;

import android.app.Application;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import me.zsr.feeder.util.LogUtil;

/**
 * @description: Global init, context.
 * @author: Match
 * @date: 15-5-13
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

        // Test
        new Thread(new Runnable() {
            @Override
            public void run() {
                RSSReader reader = new RSSReader();
                RSSFeed feed;
                try {
                    feed = reader.load("http://zhihu.com/rss");
//                    for (RSSItem item : feed.getItems()) {
//                        Log.e("Saul", "item=" + item.getContent());
//                    }
                    LogUtil.e("Feed Title=" + feed.getTitle());
                    LogUtil.e("Feed Link=" + feed.getLink());
                    LogUtil.e("Feed Description=" + feed.getDescription());
//                    LogUtil.e("Feed Link=" + feed.getPubDate().toString());
                    String categoriesStr = "";
                    for (String s : feed.getCategories()) {
                        categoriesStr += s;
                    }
                    LogUtil.e("Feed Categories=" + categoriesStr);
                    LogUtil.e("Item Title=" + feed.getItems().get(0).getTitle());
                    LogUtil.e("Item PubDate=" + feed.getItems().get(0).getPubDate().toString());
                    LogUtil.e("Item Description=" + feed.getItems().get(0).getDescription());
                    LogUtil.e("Item Content=" + feed.getItems().get(0).getContent());
                } catch (RSSReaderException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
