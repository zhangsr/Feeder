package me.zsr.feeder.data;

import java.util.List;

import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.item.OnItemLoadListener;
import me.zsr.feeder.source.OnActionListener;
import me.zsr.feeder.source.OnItemListLoadListener;
import me.zsr.feeder.source.OnSourceLoadListener;

/**
 * @description:
 * @author: Match
 * @date: 11/15/15
 */
public interface IDataModel {

    void loadAllSource(OnSourceLoadListener listener);

    void loadAllItem(OnItemListLoadListener listener, int limit);

    void loadItemList(long sourceId, OnItemListLoadListener listener, int limit);

    void loadItem(String itemTitle, OnItemLoadListener listener);

    void refreshAll(OnActionListener listener);

    void refreshSource(long sourceId, OnActionListener listener);

    void saveItem(List<FeedItem> itemList, long sourceId, OnActionListener listener);

    void saveItem(FeedItem item, OnActionListener listener);

    void deleteSource(long sourceId, OnActionListener listener);
}
