package me.zsr.feeder.ui;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;

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
    private SwipeRefreshLayout mStarLayout;
    private SwipeRefreshLayout mUnreadLayout;
    private SwipeRefreshLayout mAllLayout;
    private StickyListHeadersListView mFeedItemStarListView;
    private StickyListHeadersListView mFeedItemUnreadListView;
    private StickyListHeadersListView mFeedItemAllListView;
    private FeedItemListAdapter mStarAdapter;
    private FeedItemListAdapter mUnreadAdapter;
    private FeedItemListAdapter mAllAdapter;
    private FeedTabToolBar mTabToolBar;

    private FeedSource mFeedSource;
    private List<FeedItem> mStarFeedItemList;
    private List<FeedItem> mUnreadFeedItemList;
    private List<FeedItem> mAllFeedItemList;

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
        mStarFeedItemList = FeedDB.getInstance().getFeedItemListByStar(mFeedSource.getId(), true);
        mUnreadFeedItemList = FeedDB.getInstance().getFeedItemListByRead(mFeedSource.getId(), false);
        mAllFeedItemList = FeedDB.getInstance().getAllFeedItemList(mFeedSource.getId());
        mStarAdapter.notifyDataSetChanged(mStarFeedItemList);
        mUnreadAdapter.notifyDataSetChanged(mUnreadFeedItemList);
        mAllAdapter.notifyDataSetChanged(mAllFeedItemList);
    }

    private void initData() {
        long feedSourceId = getIntent().getExtras().getLong(App.KEY_BUNDLE_SOURCE_ID);
        mFeedSource = FeedDB.getInstance().getFeedSourceById(feedSourceId);
        mStarFeedItemList = FeedDB.getInstance().getFeedItemListByStar(feedSourceId, true);
        mUnreadFeedItemList = FeedDB.getInstance().getFeedItemListByRead(feedSourceId, false);
        mAllFeedItemList = FeedDB.getInstance().getAllFeedItemList(feedSourceId);
    }

    private void initView() {
        mStarLayout = (SwipeRefreshLayout) findViewById(R.id.feed_item_star_layout);
        mUnreadLayout = (SwipeRefreshLayout) findViewById(R.id.feed_item_unread_layout);
        mAllLayout = (SwipeRefreshLayout) findViewById(R.id.feed_item_all_layout);
        mStarAdapter = new FeedItemListAdapter(mStarFeedItemList, mFeedSource.getFavicon());
        mUnreadAdapter = new FeedItemListAdapter(mUnreadFeedItemList, mFeedSource.getFavicon());
        mAllAdapter = new FeedItemListAdapter(mAllFeedItemList, mFeedSource.getFavicon());
        mFeedItemStarListView = (StickyListHeadersListView) findViewById(R.id.feed_item_star_lv);
        mFeedItemStarListView.setAdapter(mStarAdapter);
        mFeedItemUnreadListView = (StickyListHeadersListView) findViewById(R.id.feed_item_unread_lv);
        mFeedItemUnreadListView.setLayoutTransition(new LayoutTransition());
        mFeedItemUnreadListView.setAdapter(mUnreadAdapter);
        mFeedItemAllListView = (StickyListHeadersListView) findViewById(R.id.feed_item_all_lv);
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
                notifyDataSetsChanged();
                break;
            default:
        }
    }
}
