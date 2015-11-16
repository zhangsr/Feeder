package me.zsr.feeder.item;

import me.zsr.feeder.dao.FeedItem;

/**
 * @description:
 * @author: Match
 * @date: 11/16/15
 */
public interface OnItemLoadListener {

    void success(FeedItem list);

    void error(String msg);
}
