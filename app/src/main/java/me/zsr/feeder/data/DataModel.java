package me.zsr.feeder.data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedItemDao;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.dao.FeedSourceDao;
import me.zsr.feeder.item.OnItemLoadListener;
import me.zsr.feeder.source.OnActionListener;
import me.zsr.feeder.source.OnItemListLoadListener;
import me.zsr.feeder.source.OnSourceLoadListener;
import me.zsr.feeder.util.LogUtil;

/**
 * @description:
 * @author: Match
 * @date: 11/15/15
 */
public class DataModel implements IDataModel {
    private FeedSourceDao mSourceDao;
    private FeedItemDao mItemDao;
    private FeedReader mFeedReader;

    public DataModel() {
        mSourceDao = App.getDaoSession().getFeedSourceDao();
        mItemDao = App.getDaoSession().getFeedItemDao();
        mFeedReader = new FeedReader();
    }

    @Override
    public void loadAllSource(final OnSourceLoadListener listener) {
        // TODO: 11/16/15 many request and do once as volley ?
        // TODO: 11/16/15 Handle some error
        new AsyncTask<Void, Void, List<FeedSource>>() {

            @Override
            protected List<FeedSource> doInBackground(Void... params) {
                // TODO: 11/19/15 why load source has not feeditemlist ?
                List<FeedSource> feedSourceList = mSourceDao.loadAll();
                for (FeedSource feedSource : feedSourceList) {

                    List<FeedItem> feedItemList = mItemDao.queryBuilder()
                            .where(FeedItemDao.Properties.FeedSourceId.eq(feedSource.getId()))
                            .orderDesc(FeedItemDao.Properties.Date).list();
                    feedSource.setFeedItems(feedItemList);
                }
                return feedSourceList;
            }

            @Override
            protected void onPostExecute(List<FeedSource> list) {
                super.onPostExecute(list);

                if (listener != null) {
                    listener.success(list);
                }
            }
        }.execute();
    }

    @Override
    public void loadAllItem(final OnItemListLoadListener listener, final int limit) {
        new AsyncTask<Void, Void, List<FeedItem>>() {

            @Override
            protected List<FeedItem> doInBackground(Void... params) {
                if (limit == -1) {
                    return mItemDao.queryBuilder()
                            .orderDesc(FeedItemDao.Properties.Date).list();
                } else {
                    return mItemDao.queryBuilder()
                            .limit(limit).orderDesc(FeedItemDao.Properties.Date).list();
                }
            }

            @Override
            protected void onPostExecute(List<FeedItem> list) {
                super.onPostExecute(list);

                if (listener != null) {
                    listener.success(list);
                }
            }
        }.execute();
    }

    private FeedSource loadSourceById(long sourceId) {
        List<FeedSource> list = mSourceDao.queryBuilder()
                .where(FeedSourceDao.Properties.Id.eq(sourceId)).list();
        if (list == null || list.size() == 0) {
            LogUtil.w("No FeedSource found.");
            return null;
        } if (list.size() == 1) {
            return list.get(0);
        } else {
            LogUtil.e("Somethings wrong with DB !!");
            return null;
        }
    }

    @Override
    public void loadItemList(final long sourceId, final OnItemListLoadListener listener, final int limit) {
        new AsyncTask<Void, Void, List<FeedItem>>() {

            @Override
            protected List<FeedItem> doInBackground(Void... params) {
                if (limit == -1) {
                    return mItemDao.queryBuilder()
                            .where(FeedItemDao.Properties.FeedSourceId.eq(sourceId))
                            .orderDesc(FeedItemDao.Properties.Date).list();
                } else {
                    return mItemDao.queryBuilder().limit(limit)
                            .where(FeedItemDao.Properties.FeedSourceId.eq(sourceId))
                            .orderDesc(FeedItemDao.Properties.Date).list();
                }
            }

            @Override
            protected void onPostExecute(List<FeedItem> list) {
                super.onPostExecute(list);

                if (listener != null) {
                    listener.success(list);
                }
            }
        }.execute();
    }

    @Override
    public void loadItem(String itemTitle, OnItemLoadListener listener) {
        List<FeedItem> list = mItemDao.queryBuilder().where(
                FeedItemDao.Properties.Title.eq(itemTitle)).list();
        if (listener != null) {
            if (list == null || list.size() == 0) {
                listener.error("No FeedSource found");
            } else if (list.size() == 1) {
                listener.success(list.get(0));
            } else {
                listener.error("Somethings wrong with DB !!");
            }
        }
    }

    @Override
    public void refreshAll(final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean isSuccess = false;
                for (FeedSource source : mSourceDao.loadAll()) {
                    long sourceId = source.getId();
                    try {
                        FeedSource oldFeedSource = loadSourceById(sourceId);
                        FeedSource newFeedSource = mFeedReader.load(oldFeedSource.getUrl());
                        addNewItem(newFeedSource.getFeedItems(), sourceId);
                    } catch (FeedReadException e) {
                        e.printStackTrace();
                    }
                    isSuccess = true;
                }
                return isSuccess;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);

                if (listener != null) {
                    if (isSuccess) {
                        listener.success();
                    } else {
                        listener.error("Feed read exception");
                    }
                }
            }
        }.execute();
    }

    @Override
    public void refreshSource(final long sourceId, final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean isSuccess = false;
                try {
                    FeedSource oldFeedSource = loadSourceById(sourceId);
                    FeedSource newFeedSource = mFeedReader.load(oldFeedSource.getUrl());
                    addNewItem(newFeedSource.getFeedItems(), sourceId);
                    isSuccess = true;
                } catch (FeedReadException e) {
                    e.printStackTrace();
                }
                return isSuccess;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);

                if (listener != null) {
                    if (isSuccess) {
                        listener.success();
                    } else {
                        listener.error("Feed read exception");
                    }
                }
            }
        }.execute();
    }

    @Override
    public void saveSource(final FeedSource feedSource, final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return saveSource(feedSource);
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);
                if (listener != null) {
                    if (isSuccess) {
                        listener.success();
                    } else {
                        listener.error("");
                    }
                }
            }
        }.execute();
    }

    @Override
    public boolean saveSource(FeedSource feedSource) {
        // Has same
        if (mSourceDao.queryBuilder().where(FeedSourceDao.Properties.Url.eq(
                feedSource.getUrl())).list().size() > 0) {
            return false;
        }

        mSourceDao.insertOrReplace(feedSource);
        return true;
    }

    @Override
    public void addNewItem(final List<FeedItem> itemList, final long sourceId, final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return addNewItem(itemList, sourceId);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (listener != null) {
                    if (aBoolean) {
                        listener.success();
                    } else {
                        listener.error("");
                    }
                }
            }
        }.execute();
    }

    @Override
    public void updateItem(final FeedItem item, final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                mItemDao.insertOrReplace(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (listener != null) {
                    listener.success();
                }
            }
        }.execute();
    }

    @Override
    public void updateItemList(final List<FeedItem> itemList, final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                mItemDao.insertOrReplaceInTx(itemList);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (listener != null) {
                    listener.success();
                }
            }
        }.execute();
    }

    @Override
    public boolean addNewItem(final List<FeedItem> itemList, final long sourceId) {
        List<FeedItem> itemToAdd = new ArrayList<>();
        for (FeedItem item : itemList) {
            if (mItemDao.queryBuilder().where(FeedItemDao.Properties.Title.eq(item.getTitle())).count() == 0) {
                if (item.getDate() == null) {
                    item.setDate(new Date());
                }
//                if (item.getTitle() == null) {
//                    // TODO: 11/17/15 why title null
//                    item.setTitle("");
//                }
                item.setFeedSourceId(sourceId);
                itemToAdd.add(item);
            }
        }
        mItemDao.insertInTx(itemToAdd);

        return true;
    }

    @Override
    public void deleteSource(final long sourceId, final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                mItemDao.deleteInTx(mItemDao.queryBuilder().where(
                        FeedItemDao.Properties.FeedSourceId.eq(sourceId)).list());
                mSourceDao.deleteByKey(sourceId);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (listener != null) {
                    listener.success();
                }
            }
        }.execute();
    }

    @Override
    public void deleteItem(final List<FeedItem> itemList, final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                mItemDao.deleteInTx(itemList);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (listener != null) {
                    listener.success();
                }
            }
        }.execute();
    }
}
