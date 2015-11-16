package me.zsr.feeder.source;

import java.util.List;

import me.zsr.feeder.dao.FeedItem;

/**
 * @description:
 * @author: Match
 * @date: 11/16/15
 */
public interface IItemListView {

    void updated(List<FeedItem> list);

    void showLoading();

    void hideLoading();

    void showError(String msg);

    void showBody(String itemTitle);
}
