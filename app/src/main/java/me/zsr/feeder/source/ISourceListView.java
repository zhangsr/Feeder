package me.zsr.feeder.source;

import java.util.List;

import me.zsr.feeder.dao.FeedSource;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/12/15
 */
public interface ISourceListView {

    void updated(List<FeedSource> list);

    void showLoading();

    void hideLoading();

    void showError(String msg);
}
