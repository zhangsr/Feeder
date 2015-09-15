package me.zsr.feeder.ui;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewConfiguration;
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
import me.zsr.feeder.util.CommonEvent;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class FeedItemActivity extends BaseActivity {
    private static final int MSG_DOUBLE_TAP = 0;
    private SwipeRefreshLayout mStarLayout;
    private SwipeRefreshLayout mUnreadLayout;
    private SwipeRefreshLayout mAllLayout;
    private LoadMoreHeaderListView mFeedItemStarListView;
    private LoadMoreHeaderListView mFeedItemUnreadListView;
    private LoadMoreHeaderListView mFeedItemAllListView;
    private FeedItemListAdapter mStarAdapter;
    private FeedItemListAdapter mUnreadAdapter;
    private FeedItemListAdapter mAllAdapter;
    private FeedTabToolBar mTabToolBar;

    private FeedSource mFeedSource;
    private List<FeedItem> mStarFeedItemList;
    private List<FeedItem> mUnreadFeedItemList;
    private List<FeedItem> mAllFeedItemList;

    private MyHandler mHandler = new MyHandler(this);
    private StickyListHeadersListView.OnHeaderClickListener mOnHeaderClickListener
            = new StickyListHeadersListView.OnHeaderClickListener() {
        @Override
        public void onHeaderClick(StickyListHeadersListView stickyListHeadersListView, View view,
                                  int i, long l, boolean b) {
            if (mHandler.hasMessages(MSG_DOUBLE_TAP)) {
                mHandler.removeMessages(MSG_DOUBLE_TAP);
                stickyListHeadersListView.smoothScrollToPosition(0);
            } else {
                mHandler.sendEmptyMessageDelayed(MSG_DOUBLE_TAP, ViewConfiguration.getDoubleTapTimeout());
            }
        }
    };
    private LoadMoreHeaderListView.OnLoadMoreListener mStarLoadMoreListener
            = new LoadMoreHeaderListView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            List<FeedItem> newItemList = FeedDB.getInstance().getFeedItemListByRead(
                    mFeedSource.getId(), false, mStarFeedItemList.size());
            if (newItemList.size() > 0) {
                mStarFeedItemList.addAll(newItemList);
                mStarAdapter.notifyDataSetChanged(mStarFeedItemList);
            } else {
                mFeedItemStarListView.setOnLoadMoreListener(null);
            }
            mFeedItemStarListView.completeLoadMore();
        }
    };
    private LoadMoreHeaderListView.OnLoadMoreListener mUnreadLoadMoreListener
            = new LoadMoreHeaderListView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            List<FeedItem> newItemList = FeedDB.getInstance().getFeedItemListByRead(
                    mFeedSource.getId(), false, mUnreadFeedItemList.size());
            if (newItemList.size() > 0) {
                mUnreadFeedItemList.addAll(newItemList);
                mUnreadAdapter.notifyDataSetChanged(mUnreadFeedItemList);
            } else {
                mFeedItemUnreadListView.setOnLoadMoreListener(null);
            }
            mFeedItemUnreadListView.completeLoadMore();
        }
    };
    private LoadMoreHeaderListView.OnLoadMoreListener mAllLoadMoreListener
            = new LoadMoreHeaderListView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            List<FeedItem> newItemList = FeedDB.getInstance().getFeedItemListByRead(
                    mFeedSource.getId(), false, mAllFeedItemList.size());
            if (newItemList.size() > 0) {
                mAllFeedItemList.addAll(newItemList);
                mAllAdapter.notifyDataSetChanged(mAllFeedItemList);
            } else {
                mFeedItemAllListView.setOnLoadMoreListener(null);
            }
            mFeedItemAllListView.completeLoadMore();
        }
    };

    private static class MyHandler extends Handler {
        WeakReference<FeedItemActivity> mActivity;

        MyHandler(FeedItemActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivity.get() != null) {
                switch (msg.what) {

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_item);

        initData();
        initView();
        setListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Refresh data after return from FeedBodyActivity
        notifyDataSetsChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void notifyDataSetsChanged() {
        mStarFeedItemList = FeedDB.getInstance().getFeedItemListByStar(mFeedSource.getId(), true, 0);
        mUnreadFeedItemList = FeedDB.getInstance().getFeedItemListByRead(mFeedSource.getId(), false, 0);
        mAllFeedItemList = FeedDB.getInstance().getAllFeedItemList(mFeedSource.getId(), 0);
        mStarAdapter.notifyDataSetChanged(mStarFeedItemList);
        mUnreadAdapter.notifyDataSetChanged(mUnreadFeedItemList);
        mAllAdapter.notifyDataSetChanged(mAllFeedItemList);
    }

    private void initData() {
        long feedSourceId = getIntent().getExtras().getLong(App.KEY_BUNDLE_SOURCE_ID);
        mFeedSource = FeedDB.getInstance().getFeedSourceById(feedSourceId);
        mStarFeedItemList = FeedDB.getInstance().getFeedItemListByStar(feedSourceId, true, 0);
        mUnreadFeedItemList = FeedDB.getInstance().getFeedItemListByRead(feedSourceId, false, 0);
        mAllFeedItemList = FeedDB.getInstance().getAllFeedItemList(feedSourceId, 0);
    }

    private void initView() {
        mStarLayout = (SwipeRefreshLayout) findViewById(R.id.feed_item_star_layout);
        mUnreadLayout = (SwipeRefreshLayout) findViewById(R.id.feed_item_unread_layout);
        mAllLayout = (SwipeRefreshLayout) findViewById(R.id.feed_item_all_layout);
        mStarAdapter = new FeedItemListAdapter(mStarFeedItemList, mFeedSource.getFavicon());
        mUnreadAdapter = new FeedItemListAdapter(mUnreadFeedItemList, mFeedSource.getFavicon());
        mAllAdapter = new FeedItemListAdapter(mAllFeedItemList, mFeedSource.getFavicon());
        mFeedItemStarListView = (LoadMoreHeaderListView) findViewById(R.id.feed_item_star_lv);
        mFeedItemStarListView.setAdapter(mStarAdapter);
        mFeedItemUnreadListView = (LoadMoreHeaderListView) findViewById(R.id.feed_item_unread_lv);
        mFeedItemUnreadListView.setLayoutTransition(new LayoutTransition());
        mFeedItemUnreadListView.setAdapter(mUnreadAdapter);
        mFeedItemAllListView = (LoadMoreHeaderListView) findViewById(R.id.feed_item_all_lv);
        mFeedItemAllListView.setAdapter(mAllAdapter);
        showListViewByMode(App.getInstance().mCurrentMode);

        mTabToolBar = (FeedTabToolBar) findViewById(R.id.feed_item_toolbar);
        mTabToolBar.setMode(App.getInstance().mCurrentMode);
    }

    private void setListener() {
        mStarLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedNetwork.getInstance().refresh(mFeedSource);
            }
        });
        mUnreadLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedNetwork.getInstance().refresh(mFeedSource);
            }
        });
        mAllLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedNetwork.getInstance().refresh(mFeedSource);
            }
        });
        mFeedItemStarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showBodyActivity(mStarFeedItemList.get(position));
            }
        });
        mFeedItemStarListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionsDialog(mStarFeedItemList.get(position));
                return true;
            }
        });
        mFeedItemStarListView.setOnHeaderClickListener(mOnHeaderClickListener);
        mFeedItemStarListView.setOnLoadMoreListener(mStarLoadMoreListener);
        mFeedItemUnreadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showBodyActivity(mUnreadFeedItemList.get(position));
            }
        });
        mFeedItemUnreadListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionsDialog(mUnreadFeedItemList.get(position));
                return true;
            }
        });
        mFeedItemUnreadListView.setOnHeaderClickListener(mOnHeaderClickListener);
        mFeedItemUnreadListView.setOnLoadMoreListener(mUnreadLoadMoreListener);
        mFeedItemAllListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showBodyActivity(mAllFeedItemList.get(position));
            }
        });
        mFeedItemAllListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionsDialog(mAllFeedItemList.get(position));
                return true;
            }
        });
        mFeedItemAllListView.setOnHeaderClickListener(mOnHeaderClickListener);
        mFeedItemAllListView.setOnLoadMoreListener(mAllLoadMoreListener);
        mTabToolBar.setOnTabChangedListener(new FeedTabToolBar.OnTabChangedListener() {
            @Override
            public void onTabChanged(App.Mode mode) {
                App.getInstance().mCurrentMode = mode;
                showListViewByMode(mode);
            }
        });
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
        new MaterialDialog.Builder(FeedItemActivity.this)
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
        Intent intent = new Intent(FeedItemActivity.this, FeedBodyActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showListViewByMode(App.Mode mode) {
        switch (mode) {
            case STAR:
                mStarLayout.setVisibility(View.VISIBLE);
                mUnreadLayout.setVisibility(View.GONE);
                mUnreadLayout.setRefreshing(false);
                mAllLayout.setVisibility(View.GONE);
                mAllLayout.setRefreshing(false);
                break;
            case UNREAD:
                mStarLayout.setVisibility(View.GONE);
                mStarLayout.setRefreshing(false);
                mUnreadLayout.setVisibility(View.VISIBLE);
                mAllLayout.setVisibility(View.GONE);
                mAllLayout.setRefreshing(false);
                break;
            case ALL:
                mStarLayout.setVisibility(View.GONE);
                mStarLayout.setRefreshing(false);
                mUnreadLayout.setVisibility(View.GONE);
                mUnreadLayout.setRefreshing(false);
                mAllLayout.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void onEventMainThread(CommonEvent commonEvent) {
        switch (commonEvent) {
            case FEED_DB_UPDATED:
                mStarLayout.setRefreshing(false);
                mUnreadLayout.setRefreshing(false);
                mAllLayout.setRefreshing(false);
                mFeedItemStarListView.setOnLoadMoreListener(mStarLoadMoreListener);
                mFeedItemUnreadListView.setOnLoadMoreListener(mUnreadLoadMoreListener);
                mFeedItemAllListView.setOnLoadMoreListener(mAllLoadMoreListener);
                notifyDataSetsChanged();
                break;
            default:
        }
    }
}
