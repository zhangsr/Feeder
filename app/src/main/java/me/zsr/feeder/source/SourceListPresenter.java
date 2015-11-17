package me.zsr.feeder.source;

import java.util.ArrayList;
import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.DataModel;
import me.zsr.feeder.data.IDataModel;
import me.zsr.feeder.util.NetworkUtil;

/**
 * @description:
 * @author: Match
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
        mModel.loadItemList(sourceId, new OnItemListLoadListener() {
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

    @Override
    public void clearRead() {
        mModel.loadAllSource(new OnSourceLoadListener() {
            @Override
            public void success(List<FeedSource> list) {
                List<FeedItem> listToTrash = new ArrayList<>();
                for (FeedSource feedSource : list) {
                    for (FeedItem feedItem : feedSource.getFeedItems()) {
                        if (feedItem.getRead()) {
                            feedItem.setTrash(true);
                            listToTrash.add(feedItem);
                        }
                    }
                }
                if (listToTrash.size() != 0) {
                    mModel.updateItemList(listToTrash, null);
                }
            }

            @Override
            public void error(String msg) {
            }
        });
    }
}
