package me.zsr.feeder.source;

import me.zsr.feeder.dao.FeedItem;

/**
 * @description:
 * @author: Match
 * @date: 11/16/15
 */
public interface IItemListPresenter {

    void itemSelected(FeedItem item);

    void loadMore(long sourceId);

    void reload(long sourceId);

    void refresh(long sourceId);
}
