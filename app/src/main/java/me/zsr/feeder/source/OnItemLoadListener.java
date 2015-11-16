package me.zsr.feeder.source;

import java.util.List;

import me.zsr.feeder.dao.FeedItem;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/15/15
 */
public interface OnItemLoadListener {

    void success(List<FeedItem> list);

    void error(String msg);
}
