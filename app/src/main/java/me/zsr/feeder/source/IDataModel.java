package me.zsr.feeder.source;

import java.util.List;

import me.zsr.feeder.dao.FeedItem;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/15/15
 */
public interface IDataModel {

    void loadAllSource(OnSourceLoadListener listener);

    void loadItem(long sourceId, OnItemLoadListener listener);

    void refreshAll(OnActionListener listener);

    void saveItem(List<FeedItem> itemList, long sourceId, OnActionListener listener);

    void deleteSource(long sourceId, OnActionListener listener);
}
