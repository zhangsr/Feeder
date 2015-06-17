package me.zsr.feeder.util;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.dao.FeedSourceDao;

/**
 * @description: Save data to db and send event broadcast.
 * @author: Zhangshaoru
 * @date: 15-6-11
 */
public class FeedDBUtil {
    private static FeedDBUtil sFeedDBUtil;
    private FeedSourceDao mFeedSourceDao;

    private FeedDBUtil() {}

    public static FeedDBUtil getInstance() {
        if (sFeedDBUtil == null) {
            // All init here
            sFeedDBUtil = new FeedDBUtil();
            sFeedDBUtil.mFeedSourceDao = App.getDaoSession().getFeedSourceDao();
        }
        return sFeedDBUtil;
    }

    public void saveFeedSource(FeedSource feedSource) {
        mFeedSourceDao.insertOrReplace(feedSource);
        EventBus.getDefault().post(CommonEvent.FEED_DB_UPDATED);
    }

    public FeedSource getFeedSourceById(long id) {
        List<FeedSource> list = App.getDaoSession().getFeedSourceDao().queryBuilder()
                .where(FeedSourceDao.Properties.Id.eq(id)).list();
        if (list.size() == 0) {
            LogUtil.w("No FeedSource found.");
            return null;
        } if (list.size() == 1) {
            return list.get(0);
        } else {
            LogUtil.e("Somethings wrong with DB !!");
            return null;
        }
    }

    public List<FeedSource> loadAll() {
        return mFeedSourceDao.loadAll();
    }
}
