package me.zsr.feeder.source;

import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.util.NetworkUtil;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/16/15
 */
public class ItemListPresenter implements IItemListPresenter {
    private static final int LIMIT_LOAD_ONCE = 20;
    private IItemListView mView;
    private IDataModel mModel;

    public ItemListPresenter(IItemListView view) {
        mView = view;
        mModel = new DataModel();
    }

    @Override
    public void itemSelected(final FeedItem item) {
        item.setRead(true);
        mModel.saveItem(item, new OnActionListener() {
            @Override
            public void success() {
                mView.showBody(item.getTitle());
            }

            @Override
            public void error(String msg) {
                mView.showError(msg);
            }
        });
    }

    @Override
    public void loadItem(long sourceId, int currentSize) {
        OnItemLoadListener itemLoadListener = new OnItemLoadListener() {
            @Override
            public void success(List<FeedItem> list) {
                mView.updated(list);
                mView.hideLoading();
            }

            @Override
            public void error(String msg) {
                mView.showError(msg);
                mView.hideLoading();
            }
        };
        if (sourceId == App.SOURCE_ID_ALL) {
            mModel.loadAllItem(itemLoadListener, currentSize + LIMIT_LOAD_ONCE);
        } else {
            mModel.loadItem(sourceId, itemLoadListener, currentSize + LIMIT_LOAD_ONCE);
        }
    }

    @Override
    public void refresh(final long sourceId) {
        if (NetworkUtil.isWifiEnabled(App.getInstance())) {
            OnActionListener onActionListener = new OnActionListener() {
                @Override
                public void success() {
                    loadItem(sourceId, 0);
                }

                @Override
                public void error(String msg) {
                    mView.showError(msg);
                    mView.hideLoading();
                }
            };
            if (sourceId == App.SOURCE_ID_ALL) {
                mModel.refreshAll(onActionListener);
            } else {
                mModel.refreshSource(sourceId, onActionListener);
            }
        } else {
            mView.showError("Wi-Fi is disabled");
        }

    }
}
