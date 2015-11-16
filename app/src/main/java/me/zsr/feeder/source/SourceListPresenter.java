package me.zsr.feeder.source;

import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.util.NetworkUtil;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/12/15
 */
public class SourceListPresenter implements ISourceListPresenter {
    private ISourceListView mView;
    private OnSourceSelectedListener mSourceSelectedListener;
    private IDataModel mModel;

    public SourceListPresenter(ISourceListView view) {
        mView = view;
        mModel = new DataModel();
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
        mModel.loadAllSource(new OnSourceLoadListener() {
            @Override
            public void success(List<FeedSource> list) {
                mView.updated(list);
                mView.hideLoading();
            }

            @Override
            public void error(String msg) {
                mView.showError(msg);
                mView.hideLoading();
            }
        });
    }

    @Override
    public void refresh() {
        if (NetworkUtil.isWifiEnabled(App.getInstance())) {
            mModel.refreshAll(new OnActionListener() {
                @Override
                public void success() {
                    loadSource();
                }

                @Override
                public void error(String msg) {
                    mView.showError(msg);
                    mView.hideLoading();
                }
            });
        } else {
            mView.showError("Wi-Fi is disabled");
        }
    }

    @Override
    public void markAsRead(final long sourceId) {
        mModel.loadItem(sourceId, new OnItemLoadListener() {
            @Override
            public void success(List<FeedItem> list) {
                for (FeedItem item : list) {
                    item.setRead(true);
                }
                mModel.saveItem(list, sourceId, new OnActionListener() {
                    @Override
                    public void success() {
                        loadSource();
                    }

                    @Override
                    public void error(String msg) {
                        mView.showError(msg);
                    }
                });
            }

            @Override
            public void error(String msg) {
                mView.showError(msg);
            }
        }, -1);
    }

    @Override
    public void deleteSource(long sourceId) {
        mModel.deleteSource(sourceId, new OnActionListener() {
            @Override
            public void success() {
                loadSource();
            }

            @Override
            public void error(String msg) {
                mView.showError(msg);
            }
        });
    }
}
