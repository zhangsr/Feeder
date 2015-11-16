package me.zsr.feeder.data;

import java.util.Date;
import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedItemDao;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.dao.FeedSourceDao;
import me.zsr.feeder.util.LogUtil;

/**
 * @description: Save data to db and send event broadcast.
 * @author: Match
 * @date: 15-6-11
 */
public class FeedDB {
    private static FeedDB sFeedDB;
    private FeedSourceDao mFeedSourceDao;
    private FeedItemDao mFeedItemDao;

    private FeedDB() {}

    public static FeedDB getInstance() {
        if (sFeedDB == null) {
            // All init here
            sFeedDB = new FeedDB();
            sFeedDB.mFeedSourceDao = App.getDaoSession().getFeedSourceDao();
            sFeedDB.mFeedItemDao = App.getDaoSession().getFeedItemDao();
        }
        return sFeedDB;
    }

    public void saveFeedSource(FeedSource feedSource) {
        if (feedSource.getId() == null) { // new fetch
            if (mFeedSourceDao.queryBuilder().where(FeedSourceDao.Properties.Title.eq(
                    feedSource.getTitle())).list().size() == 0) {
                mFeedSourceDao.insertOrReplace(feedSource);
            } else { // Has same

            }
        } else { // already exist
            mFeedSourceDao.update(feedSource);
        }
    }

    public void saveFeedItem(FeedItem feedItem, long sourceId) {
        feedItem.setFeedSourceId(sourceId);
        mFeedItemDao.insertOrReplace(feedItem);
//        if (feedItem.getId() == null) { // new fetch
//            if (mFeedItemDao.queryBuilder().where(FeedItemDao.Properties.Title.eq(
//                    feedItem.getTitle())).list().size() == 0) {
//                mFeedItemDao.insertOrReplace(feedItem);
//            } else { // Has same
//
//            }
//        } else { // already exist
//            mFeedItemDao.update(feedItem);
//        }
    }

    public int countItemByRead(long sourceId, boolean read) {
        if (sourceId == App.SOURCE_ID_ALL) {
            return mFeedItemDao.queryBuilder().where(
                    FeedItemDao.Properties.Read.eq(read)).list().size();
        } else {
            return mFeedItemDao.queryBuilder().where(
                    FeedItemDao.Properties.FeedSourceId.eq(sourceId),
                    FeedItemDao.Properties.Read.eq(read)).list().size();
        }
    }

    public int countItemByStar(long sourceId, boolean star) {
        return mFeedItemDao.queryBuilder().where(
                FeedItemDao.Properties.FeedSourceId.eq(sourceId),
                FeedItemDao.Properties.Star.eq(star)).list().size();
    }

    public void saveFeedItem(final List<FeedItem> feedItemList, final long sourceId) {
        App.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = feedItemList.size() - 1; i >= 0; i--) {
                    FeedItem item = feedItemList.get(i);
                    if (item.getDate() == null) {
                        item.setDate(new Date());
                    }
                    if (item.getTitle() == null) {
                        item.setTitle("");
                    }
                    saveFeedItem(item, sourceId);
                }
            }
        });
//        EventBus.getDefault().post(CommonEvent.FEED_DB_UPDATED);
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

    public FeedItem getFeedItemByTitle(String title) {
        List<FeedItem> list = App.getDaoSession().getFeedItemDao().queryBuilder()
                .where(FeedItemDao.Properties.Title.eq(title)).list();
        if (list.size() == 0) {
            LogUtil.w("No FeedItem found.");
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

    public boolean hasSource(String url) {
        return App.getDaoSession().getFeedSourceDao().queryBuilder()
                .where(FeedSourceDao.Properties.Url.eq(url)).list().size() > 0;
    }
}
