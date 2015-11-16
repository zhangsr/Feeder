package me.zsr.feeder.source;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.base.BaseFragment;

/**
 * @description:
 * @author: Match
 * @date: 10/28/15
 */
public class SourceListFragment extends BaseFragment implements ISourceListView {
    private static SourceListFragment sInstance;

    private ISourceListPresenter mPresenter;
    private View mRootView;
    private ListView mListView;
    private SourceListAdapter mAdapter;
    private SwipeRefreshLayout mPullRefreshLayout;
    private View mAllHeaderView;

    public static SourceListFragment getInstance() {
        if (sInstance == null) {
            sInstance = new SourceListFragment();
        }
        return sInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_source_list, container, false);
        initView();
        setListener();

        mPresenter.refresh();
        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // FIXME: 11/15/15 something wild to init here
        mPresenter = new SourceListPresenter(this);
        mPresenter.setOnSourceSelectedListener((OnSourceSelectedListener) activity);
    }

    private void initView() {
        mListView = (ListView) mRootView.findViewById(R.id.feed_lv);

        mAllHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.source_list_item, null);
        ((NetworkImageView) mAllHeaderView.findViewById(R.id.source_favicon_img)).setDefaultImageResId(R.drawable.ic_all);
        ((TextView) mAllHeaderView.findViewById(R.id.source_title_txt)).setText(R.string.all);
        mListView.addHeaderView(mAllHeaderView);

        mPullRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.feed_pull_to_refresh_layout);
    }

    private void setListener() {
        mPullRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refresh();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mPresenter.sourceSelected(App.SOURCE_ID_ALL);
                } else {
                    mPresenter.sourceSelected(
                            ((FeedSource) parent.getAdapter().getItem(position)).getId());
                }
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return false;
                } else {
                    final FeedSource feedSource = (FeedSource) parent.getAdapter().getItem(position);
                    List<CharSequence> menuList = new ArrayList<>();
                    for (FeedItem item : feedSource.getFeedItems()) {
                        if (!item.getRead()) {
                            menuList.add(getString(R.string.mark_as_read));
                            break;
                        }
                    }
                    menuList.add(getString(R.string.remove_subscription));
                    new MaterialDialog.Builder(getActivity())
                            .title(feedSource.getTitle())
                            .items(menuList.toArray(new CharSequence[menuList.size()]))
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int i,
                                                        CharSequence charSequence) {
                                    switch (i) {
                                        case 0:
                                            mPresenter.markAsRead(feedSource.getId());
                                            break;
                                        case 1:
                                            mPresenter.deleteSource(feedSource.getId());
                                            break;
                                    }
                                }
                            }).show();
                    return true;
                }
            }
        });
    }


    @Override
    public void updated(List<FeedSource> list) {
        if (mAdapter == null) {
            mAdapter = new SourceListAdapter(list);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged(list);
        }

        int allCount = 0;
        for (FeedSource source : list) {
            allCount += source.getFeedItems().size();
        }
        ((TextView) mAllHeaderView.findViewById(R.id.source_item_num_txt)).setText("" + allCount);
    }

    @Override
    public void showLoading() {
        mPullRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mPullRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
