package me.zsr.feeder.data;

import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedAccount;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
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

    void loadAllSource(FeedAccount account, OnSourceLoadListener listener);

    void loadAllItem(FeedAccount account, OnItemListLoadListener listener, int limit);

    void loadItemList(long sourceId, OnItemListLoadListener listener, int limit);

    void loadItem(Long id, OnItemLoadListener listener);

    void refreshAll(FeedAccount account, OnActionListener listener);

    void refreshSource(long sourceId, OnActionListener listener);

    void saveSource(FeedAccount account, FeedSource feedSource, OnActionListener listener);

    boolean saveSource(FeedAccount account, FeedSource feedSource);

    void addNewItem(FeedAccount account, List<FeedItem> itemList, long sourceId, OnActionListener listener);

    boolean addNewItem(FeedAccount account, List<FeedItem> itemList, long sourceId);

    void updateItem(FeedItem item, OnActionListener listener);

    void updateItemList(List<FeedItem> itemList, OnActionListener listener);

    void deleteSource(FeedAccount account, long sourceId, OnActionListener listener);

    void deleteItem(List<FeedItem> itemList, OnActionListener listener);
}
