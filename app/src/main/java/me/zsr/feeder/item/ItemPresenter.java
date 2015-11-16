package me.zsr.feeder.item;

import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.data.DataModel;
import me.zsr.feeder.data.IDataModel;

/**
 * @description:
 * @author: Match
 * @date: 11/16/15
 */
public class ItemPresenter implements IItemPresenter {
    private IItemView mView;
    private IDataModel mModel;

    public ItemPresenter(IItemView view) {
        mView = view;
        mModel = new DataModel();
    }

    @Override
    public void loadItem(final String itemTitle) {
        mModel.loadItem(itemTitle, new OnItemLoadListener() {
            @Override
            public void success(FeedItem item) {
                mView.updated(item);
            }

            @Override
            public void error(String msg) {
                mView.showError(msg);
            }
        });
    }
}
