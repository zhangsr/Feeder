package me.zsr.feeder.source;

import android.animation.LayoutTransition;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.item.ItemActivity;
import me.zsr.feeder.view.LoadMoreHeaderListView;
import me.zsr.feeder.util.CommonEvent;

/**
 * @description:
 * @author: Match
 * @date: 11/3/15
 */
public class ItemListFragment extends Fragment implements IItemListView {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreHeaderListView mListView;
    private ItemListAdapter mAdapter;
    private View mRootView;

    private IItemListPresenter mPresenter;

    private long mFeedSourceId;

    private LoadMoreHeaderListView.OnLoadMoreListener mLoadMoreListener
            = new LoadMoreHeaderListView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            mPresenter.loadMore(mFeedSourceId);
        }
    };

    @Override
    public void updated(List<FeedItem> list) {
        if (mAdapter == null) {
            mAdapter = new ItemListAdapter(list);
            mListView.setAdapter(mAdapter);
            mListView.setEmptyView(new View(getActivity()));
        } else {
            mAdapter.notifyDataSetChanged(list);
        }
        mListView.setOnLoadMoreListener(mLoadMoreListener);
    }

    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
        mListView.completeLoadMore();
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBody(String itemTitle) {
        Bundle bundle = new Bundle();
        bundle.putString(App.KEY_BUNDLE_ITEM_TITLE, itemTitle);
        Intent intent = new Intent(getActivity(), ItemActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public static ItemListFragment newInstance(long sourceId) {
        ItemListFragment fragment = new ItemListFragment();

        Bundle args = new Bundle();
        args.putLong("sourceId", sourceId);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // init data
        mFeedSourceId = getArguments().getLong("sourceId");

        // init view
        mRootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        mListView = (LoadMoreHeaderListView) mRootView.findViewById(R.id.item_lv);
        mListView.setLayoutTransition(new LayoutTransition());

        // set listener
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refresh(mFeedSourceId);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.itemSelected(((FeedItem) parent.getAdapter().getItem(position)));
            }
        });
        mListView.setOnLoadMoreListener(mLoadMoreListener);

        mPresenter = new ItemListPresenter(this);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter == null) {
            mPresenter.loadMore(mFeedSourceId);
        } else {
            mPresenter.reload(mFeedSourceId);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(CommonEvent commonEvent) {
        switch (commonEvent) {
            case SOURCE_TOOLBAR_DOUBLE_CLICK:
                mListView.smoothScrollToPosition(0);
                break;
            default:
        }
    }

    public long getShownSourceId() {
        return getArguments().getLong("sourceId");
    }
}
