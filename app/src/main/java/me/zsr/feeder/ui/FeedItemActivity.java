package me.zsr.feeder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.util.FeedDBUtil;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class FeedItemActivity extends BaseActivity {
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

    private void initData() {
        long feedSourceId = getIntent().getExtras().getLong(App.KEY_BUNDLE_SOURCE_ID);
        mFeedSource = FeedDBUtil.getInstance().getFeedSourceById(feedSourceId);
        mStarFeedItemList = FeedDBUtil.getInstance().getFeedItemListByStar(feedSourceId, true);
        mUnreadFeedItemList = FeedDBUtil.getInstance().getFeedItemListByRead(feedSourceId, false);
        mAllFeedItemList = FeedDBUtil.getInstance().getAllFeedItemList(feedSourceId);
    }

    private void initView() {
        mStarAdapter = new FeedItemListAdapter(mStarFeedItemList, mFeedSource.getFavicon());
        mUnreadAdapter = new FeedItemListAdapter(mUnreadFeedItemList, mFeedSource.getFavicon());
        mAllAdapter = new FeedItemListAdapter(mAllFeedItemList, mFeedSource.getFavicon());
        mFeedItemStarListView = (StickyListHeadersListView) findViewById(R.id.feed_item_star_lv);
        mFeedItemStarListView.setAdapter(mStarAdapter);
        mFeedItemUnreadListView = (StickyListHeadersListView) findViewById(R.id.feed_item_unread_lv);
        mFeedItemUnreadListView.setAdapter(mUnreadAdapter);
        mFeedItemAllListView = (StickyListHeadersListView) findViewById(R.id.feed_item_all_lv);
        mFeedItemAllListView.setAdapter(mAllAdapter);
        showListViewByMode(App.getInstance().mCurrentMode);

        mTabToolBar = (FeedTabToolBar) findViewById(R.id.feed_item_toolbar);
        mTabToolBar.setMode(App.getInstance().mCurrentMode);
    }

    private void setListener() {
        mFeedItemStarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetail(mStarFeedItemList.get(position));
            }
        });
        mFeedItemUnreadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetail(mUnreadFeedItemList.get(position));
            }
        });
        mFeedItemAllListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetail(mAllFeedItemList.get(position));
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

    private void showDetail(FeedItem feedItem) {
        feedItem.setRead(true);
        Bundle bundle = new Bundle();
        FeedDBUtil.getInstance().saveFeedItem(feedItem);
        bundle.putLong(App.KEY_BUNDLE_ITEM_ID, feedItem.getId());
        Intent intent = new Intent(FeedItemActivity.this, FeedBodyActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showListViewByMode(App.Mode mode) {
        switch (mode) {
            case STAR:
                mFeedItemStarListView.setVisibility(View.VISIBLE);
                mFeedItemUnreadListView.setVisibility(View.GONE);
                mFeedItemAllListView.setVisibility(View.GONE);
                break;
            case UNREAD:
                mFeedItemStarListView.setVisibility(View.GONE);
                mFeedItemUnreadListView.setVisibility(View.VISIBLE);
                mFeedItemAllListView.setVisibility(View.GONE);
                break;
            case ALL:
                mFeedItemStarListView.setVisibility(View.GONE);
                mFeedItemUnreadListView.setVisibility(View.GONE);
                mFeedItemAllListView.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }
}
