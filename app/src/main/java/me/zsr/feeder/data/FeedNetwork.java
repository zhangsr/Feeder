package me.zsr.feeder.data;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.util.CommonEvent;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 8/28/15
 */
public class FeedNetwork {
    private static final int MSG_NOTIFY_FEED_DB_UPDATED = 0;
    private static final int DELAY_NOTIFY_FEED_DB_UPDATED = 1000;
    private static FeedNetwork sInstance;
    private FeedReader mFeedReader;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_NOTIFY_FEED_DB_UPDATED:
                    EventBus.getDefault().post(CommonEvent.FEED_DB_UPDATED);
                    break;
                default:
            }
        }
    };

    private FeedNetwork() {}

    public static FeedNetwork getInstance() {
        if (sInstance == null) {
            sInstance = new FeedNetwork();
            sInstance.mFeedReader = new FeedReader();
        }
        return sInstance;
    }

    public void refreshAll() {
        List<FeedSource> feedSourceList = FeedDB.getInstance().loadAll();
        for (FeedSource source : feedSourceList) {
            refresh(source);
        }
    }

    public void refresh(final FeedSource feedSource) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FeedSource newFeedSource = mFeedReader.load(feedSource.getUrl());
                    FeedDB.getInstance().saveFeedItem(newFeedSource.getFeedItems(), feedSource.getId());
                    notifyUI();
                } catch (FeedReadException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public interface OnVerifyListener {
        void onResult(boolean isValid, FeedSource feedSource);
    }

    public void verifySource(final String url, final OnVerifyListener listener) {
        new AsyncTask<Void, Void, FeedSource>() {
            @Override
            protected FeedSource doInBackground(Void... params) {
                try {
                    FeedSource newFeedSource = mFeedReader.load(url);
                    if (TextUtils.isEmpty(newFeedSource.getTitle())) {
                        return null;
                    }
                    // TODO: 8/29/15 need more verify ?
                    return newFeedSource;
                } catch (FeedReadException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(FeedSource feedSource) {
                super.onPostExecute(feedSource);
                if (listener != null) {
                    listener.onResult(feedSource != null, feedSource);
                }
            }
        }.execute();
    }

    public interface OnAddListener {
        void onError(String msg);
    }

    public void addSource(final String url, final String reTitle, OnAddListener listener) {
        if (FeedDB.getInstance().hasSource(url)) {
            if (listener != null) {
                listener.onError("源已经添加过了");
            }
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FeedSource feedSource = mFeedReader.load(url);
                    feedSource.setTitle(reTitle);
                    FeedDB.getInstance().saveFeedSource(feedSource);
                    FeedDB.getInstance().saveFeedItem(feedSource.getFeedItems(), feedSource.getId());
                    notifyUI();
                } catch (FeedReadException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void addSource(final FeedSource feedSource, OnAddListener listener) {
        if (FeedDB.getInstance().hasSource(feedSource.getUrl())) {
            if (listener != null) {
                listener.onError("该源已经添加过了");
            }
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                FeedDB.getInstance().saveFeedSource(feedSource);
                FeedDB.getInstance().saveFeedItem(feedSource.getFeedItems(), feedSource.getId());
                notifyUI();
                return null;
            }
        }.execute();
    }

    private void notifyUI() {
        mHandler.removeMessages(MSG_NOTIFY_FEED_DB_UPDATED);
        mHandler.sendEmptyMessageDelayed(MSG_NOTIFY_FEED_DB_UPDATED, DELAY_NOTIFY_FEED_DB_UPDATED);
    }
}
