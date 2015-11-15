package me.zsr.feeder.source;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedDB;
import me.zsr.feeder.data.FeedNetwork;
import me.zsr.feeder.util.CommonEvent;
import me.zsr.feeder.util.NetworkUtil;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/12/15
 */
public class SourceListPresenter implements ISourceListPresenter {
    private ISourceListView mView;
    private OnSourceSelectedListener mSourceSelectedListener;
    private List<FeedSource> mSourceList = new ArrayList<>();

    public SourceListPresenter(ISourceListView view) {
        mView = view;
        EventBus.getDefault().register(this);
    }

    @Override
    public void setOnSourceSelectedListener(OnSourceSelectedListener listener) {
        mSourceSelectedListener = listener;
    }

    @Override
    public void sourceSelected(long sourceId) {
        if (mSourceSelectedListener != null) {
            mSourceSelectedListener.onSourceSelected(sourceId);
        }
    }

    @Override
    public void loadSource() {
        // TODO: 11/13/15 load async
        mSourceList = FeedDB.getInstance().loadAll();
        mView.updated(mSourceList);
        mView.hideLoading();
    }

    @Override
    public void refresh() {
        if (NetworkUtil.isWifiEnabled(App.getInstance())) {
            FeedNetwork.getInstance().refreshAll();
        } else {
            mView.showError("Wi-Fi is disabled");
        }
    }

    @Override
    public void markAllAsRead(long sourceId) {
        FeedDB.getInstance().markAllAsRead(sourceId);
        loadSource();
    }

    @Override
    public void deleteSource(long sourceId) {
        FeedDB.getInstance().deleteSource(sourceId);
        loadSource();
    }

    public void onEventMainThread(CommonEvent commonEvent) {
        switch (commonEvent) {
            case FEED_DB_UPDATED:
                loadSource();
                break;
            default:
        }
    }
}
