package me.zsr.feeder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.util.FeedDBUtil;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class FeedItemActivity extends BaseActivity {
    private StickyListHeadersListView mFeedItemListView;
    private FeedTabToolBar mTabToolBar;

    private FeedSource mFeedSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_item);

        initData();
        initView();
        setListener();
    }

    private void initData() {
        mFeedSource = FeedDBUtil.getInstance().getFeedSourceById(
                getIntent().getExtras().getLong(App.KEY_BUNDLE_SOURCE_ID));
    }

    private void initView() {
        mFeedItemListView = (StickyListHeadersListView) findViewById(R.id.feed_item_lv);
        FeedItemListAdapter adapter = new FeedItemListAdapter(mFeedSource);
        mFeedItemListView.setAdapter(adapter);
        mTabToolBar = (FeedTabToolBar) findViewById(R.id.feed_item_toolbar);
        mTabToolBar.setMode(App.getInstance().mCurrentMode);
    }

    private void setListener() {
        mFeedItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                FeedItem feedItem = mFeedSource.getFeedItems().get(position);
                feedItem.setRead(true);
                FeedDBUtil.getInstance().saveFeedItem(feedItem);
                bundle.putLong(App.KEY_BUNDLE_ITEM_ID, feedItem.getId());
                Intent intent = new Intent(FeedItemActivity.this , FeedBodyActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mTabToolBar.setOnTabChangedListener(new FeedTabToolBar.OnTabChangedListener() {
            @Override
            public void onTabChanged(App.Mode mode) {

            }
        });
    }
}
