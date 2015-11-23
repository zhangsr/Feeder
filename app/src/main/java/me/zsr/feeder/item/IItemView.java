package me.zsr.feeder.item;

import me.zsr.feeder.dao.FeedItem;

/**
 * @description:
 * @author: Match
 * @date: 11/16/15
 */
public interface IItemView {

    void updated(FeedItem item);

    void showError(String msg);

    void showMsg(String msg);
}
