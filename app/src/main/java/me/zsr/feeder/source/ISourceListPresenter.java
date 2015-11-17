package me.zsr.feeder.source;

/**
 * @description:
 * @author: Match
 * @date: 11/12/15
 */
public interface ISourceListPresenter {

    void setOnSourceSelectedListener(OnSourceSelectedListener listener);

    void sourceSelected(long sourceId);

    void loadSource();

    void refresh();

    void markAsRead(long sourceId);

    void deleteSource(long sourceId);

    void clearRead();
}
