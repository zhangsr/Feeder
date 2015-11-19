package me.zsr.feeder.source;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.DataModel;
import me.zsr.feeder.data.IDataModel;
import me.zsr.feeder.util.CommonEvent;
import me.zsr.feeder.util.DateUtil;
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
                mModel.updateItemList(list, new OnActionListener() {
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
    public void clear() {
        mModel.loadAllSource(new OnSourceLoadListener() {
            @Override
            public void success(List<FeedSource> list) {
                // FIXME: 11/19/15 bad performance
                List<FeedItem> listToTrash = new ArrayList<>();
                List<FeedItem> listToDelete = new ArrayList<>();
                for (FeedSource feedSource : list) {
                    List<FeedItem> feedItemList = feedSource.getFeedItems();
                    for (FeedItem feedItem : feedItemList) {
                        if (feedItem.getTrash()
                                && !DateUtil.isSameDay(feedItem.getLastShownDate(),
                                feedItemList.get(0).getLastShownDate())) {
                            listToDelete.add(feedItem);
                        } else if (feedItem.getRead()) {
                            feedItem.setTrash(true);
                            listToTrash.add(feedItem);
                        }
                    }
                }
                if (listToTrash.size() != 0) {
                    mModel.updateItemList(listToTrash, null);
                }
                // TODO: 11/19/15 add analysis to verify work or not
                if (listToDelete.size() != 0) {
                    mModel.deleteItem(listToDelete, null);
                }
            }

            @Override
            public void error(String msg) {
            }
        });
    }

    public void onEventMainThread(CommonEvent commonEvent) {
        switch (commonEvent) {
            case ITEM_LIST_REFRESH_SUCCESS:
                loadSource();
                break;
        }
    }
}
