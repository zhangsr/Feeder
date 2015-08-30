package me.zsr.feeder.data;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.avos.avoscloud.AVObject;

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
        void onResult(boolean isValid);
    }

    // TODO: 8/29/15 Add to avos in caller
    public void verifySource(final String url, final OnVerifyListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    FeedSource newFeedSource = mFeedReader.load(url);

                    AVObject feedSourceObj = new AVObject("FeedSource");
                    feedSourceObj.put("title", newFeedSource.getTitle());
                    feedSourceObj.put("url", url);
                    feedSourceObj.put("link", newFeedSource.getLink());
                    feedSourceObj.saveInBackground();
                    // TODO: 8/29/15 need more verify ?
                    return true;
                } catch (FeedReadException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean isValid) {
                super.onPostExecute(isValid);
                if (listener != null) {
                    listener.onResult(isValid);
                }
            }
        }.execute();
    }

    public interface OnAddListener {
        void onError(String msg);
    }

    public void addSource(final String url, OnAddListener listener) {
        if (FeedDB.getInstance().hasSource(url)) {
            if (listener != null) {
                listener.onError("Source reduplicated");
            }
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FeedSource feedSource = mFeedReader.load(url);
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

    private void notifyUI() {
        mHandler.removeMessages(MSG_NOTIFY_FEED_DB_UPDATED);
        mHandler.sendEmptyMessageDelayed(MSG_NOTIFY_FEED_DB_UPDATED, DELAY_NOTIFY_FEED_DB_UPDATED);
    }
}
