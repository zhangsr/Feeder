package me.zsr.feeder.source;

import me.zsr.feeder.dao.FeedItem;

/**
 * @description:
 * @author: Match
 * @date: 11/16/15
 */
public interface IItemListPresenter {

    void itemSelected(FeedItem item);

    void loadItem(long sourceId, int currentSize);

    void refresh(long sourceId);
}
