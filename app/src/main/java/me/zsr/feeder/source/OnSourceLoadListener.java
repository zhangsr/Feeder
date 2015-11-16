package me.zsr.feeder.source;

import java.util.List;

import me.zsr.feeder.dao.FeedSource;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/15/15
 */
public interface OnSourceLoadListener {

    void success(List<FeedSource> list);

    void error(String msg);
}
