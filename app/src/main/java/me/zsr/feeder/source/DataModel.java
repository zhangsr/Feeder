package me.zsr.feeder.source;

import android.os.AsyncTask;

import java.util.Date;
import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedItemDao;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.dao.FeedSourceDao;
import me.zsr.feeder.data.FeedReadException;
import me.zsr.feeder.data.FeedReader;
import me.zsr.feeder.util.LogUtil;

/**
 * @description:
 * @author: Zhangshaoru
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
                return mSourceDao.loadAll();
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
    public void loadAllItem(final OnItemLoadListener listener, final int limit) {
        new AsyncTask<Void, Void, List<FeedItem>>() {

            @Override
            protected List<FeedItem> doInBackground(Void... params) {
                if (limit == -1) {
                    return mItemDao.queryBuilder().list();
                } else {
                    return mItemDao.queryBuilder().limit(limit).list();
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
    public void loadItem(final long sourceId, final OnItemLoadListener listener, final int limit) {
        new AsyncTask<Void, Void, List<FeedItem>>() {

            @Override
            protected List<FeedItem> doInBackground(Void... params) {
                if (limit == -1) {
                    return mItemDao.queryBuilder().where(
                            FeedItemDao.Properties.FeedSourceId.eq(sourceId)).list();
                } else {
                    return mItemDao.queryBuilder().limit(limit).where(
                            FeedItemDao.Properties.FeedSourceId.eq(sourceId)).list();
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
                        saveItem(newFeedSource.getFeedItems(), sourceId);
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
                    saveItem(newFeedSource.getFeedItems(), sourceId);
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
    public void saveItem(final List<FeedItem> itemList, final long sourceId, final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                saveItem(itemList, sourceId);
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
    public void saveItem(final FeedItem item, final OnActionListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (item.getDate() == null) {
                    item.setDate(new Date());
                }
                if (item.getTitle() == null) {
                    item.setTitle("");
                }
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

    private void saveItem(final List<FeedItem> itemList, final long sourceId) {
        for (FeedItem item : itemList) {
            if (item.getDate() == null) {
                item.setDate(new Date());
            }
            if (item.getTitle() == null) {
                item.setTitle("");
            }
            item.setFeedSourceId(sourceId);
        }
        mItemDao.insertOrReplaceInTx(itemList);
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
}
