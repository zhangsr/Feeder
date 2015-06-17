package me.zsr.feeder.ui;

import android.app.Activity;
import android.os.Bundle;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.util.FeedDBUtil;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class FeedItemActivity extends Activity {
    private StickyListHeadersListView mFeedItemListView;
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
    }

    private void setListener() {

    }
}
