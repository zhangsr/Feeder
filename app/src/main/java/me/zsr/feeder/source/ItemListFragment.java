package me.zsr.feeder.source;

import android.animation.LayoutTransition;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedDB;
import me.zsr.feeder.data.FeedNetwork;
import me.zsr.feeder.item.ItemActivity;
import me.zsr.feeder.view.LoadMoreHeaderListView;
import me.zsr.feeder.util.CommonEvent;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/3/15
 */
public class ItemListFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreHeaderListView mListView;
    private ItemListAdapter mAdapter;
    private View mRootView;

    private FeedSource mFeedSource;
    private List<FeedItem> mItemList;

    private MyHandler mHandler = new MyHandler(this);
    private LoadMoreHeaderListView.OnLoadMoreListener mLoadMoreListener
            = new LoadMoreHeaderListView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            List<FeedItem> newItemList = FeedDB.getInstance().getFeedItemListByRead(
                    mFeedSource.getId(), false, mItemList.size());
            if (newItemList.size() > 0) {
                mItemList.addAll(newItemList);
                mAdapter.notifyDataSetChanged(mItemList);
            } else {
                mListView.setOnLoadMoreListener(null);
            }
            mListView.completeLoadMore();
        }
    };

    private static class MyHandler extends Handler {
        WeakReference<ItemListFragment> mmFragment;

        MyHandler(ItemListFragment fragment) {
            mmFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mmFragment.get() != null) {
                switch (msg.what) {

                }
            }
        }
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
        long feedSourceId = getArguments().getLong("sourceId");
        mFeedSource = FeedDB.getInstance().getFeedSourceById(feedSourceId);
        mItemList = FeedDB.getInstance().getFeedItemListByRead(feedSourceId, false, 0);

        // init view
        mRootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        mAdapter = new ItemListAdapter(mItemList, mFeedSource.getFavicon());
        mListView = (LoadMoreHeaderListView) mRootView.findViewById(R.id.item_lv);
        mListView.setLayoutTransition(new LayoutTransition());
        mListView.setAdapter(mAdapter);

        // set listener
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedNetwork.getInstance().refresh(mFeedSource);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showBodyActivity(mItemList.get(position));
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionsDialog(mItemList.get(position));
                return true;
            }
        });
        mListView.setOnLoadMoreListener(mLoadMoreListener);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void showOptionsDialog(final FeedItem feedItem) {
        List<CharSequence> menuList = new ArrayList<>();
        if (feedItem.getStar()) {
            menuList.add(getString(R.string.remove_star_mark));
        } else {
            menuList.add(getString(R.string.add_star_mark));
        }
        if (feedItem.getRead()) {
            menuList.add(getString(R.string.mark_as_unread));
        } else {
            menuList.add(getString(R.string.mark_as_read));
        }
        new MaterialDialog.Builder(getActivity())
                .items(menuList.toArray(new CharSequence[menuList.size()]))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i,
                                            CharSequence charSequence) {
                        switch (i) {
                            case 0:
                                feedItem.setStar(!feedItem.getStar());
                                break;
                            case 1:
                                feedItem.setRead(!feedItem.getRead());
                                break;
                            default:
                        }

                        FeedDB.getInstance().saveFeedItem(feedItem, mFeedSource.getId());
                        notifyDataSetsChanged();
                    }
                }).show();
    }

    private void showBodyActivity(FeedItem feedItem) {
        feedItem.setRead(true);
        Bundle bundle = new Bundle();
        FeedDB.getInstance().saveFeedItem(feedItem, mFeedSource.getId());
        bundle.putLong(App.KEY_BUNDLE_ITEM_ID, feedItem.getId());
        Intent intent = new Intent(getActivity(), ItemActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onEventMainThread(CommonEvent commonEvent) {
        switch (commonEvent) {
            case FEED_DB_UPDATED:
                mSwipeRefreshLayout.setRefreshing(false);
                mListView.setOnLoadMoreListener(mLoadMoreListener);
                notifyDataSetsChanged();
                break;
            case SOURCE_TOOLBAR_DOUBLE_CLICK:
                mListView.smoothScrollToPosition(0);
                break;
            default:
        }
    }

    private void notifyDataSetsChanged() {
        mItemList = FeedDB.getInstance().getFeedItemListByRead(mFeedSource.getId(), false, 0);
        mAdapter.notifyDataSetChanged(mItemList);
    }

    public long getShownSourceId() {
        return getArguments().getLong("sourceId");
    }
}
