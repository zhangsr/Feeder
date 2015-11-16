package me.zsr.feeder.source;

import java.util.List;

import me.zsr.feeder.dao.FeedItem;

/**
 * @description:
 * @author: Match
 * @date: 11/15/15
 */
public interface IDataModel {

    void loadAllSource(OnSourceLoadListener listener);

    void loadAllItem(OnItemLoadListener listener, int limit);

    void loadItem(long sourceId, OnItemLoadListener listener, int limit);

    void refreshAll(OnActionListener listener);

    void refreshSource(long sourceId, OnActionListener listener);

    void saveItem(List<FeedItem> itemList, long sourceId, OnActionListener listener);

    void saveItem(FeedItem item, OnActionListener listener);

    void deleteSource(long sourceId, OnActionListener listener);
}
