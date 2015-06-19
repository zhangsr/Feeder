package me.zsr.feeder.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

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
        mFeedItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putLong(App.KEY_BUNDLE_ITEM_ID, mFeedSource.getFeedItems().get(position).getId());
                Intent intent = new Intent(FeedItemActivity.this , FeedBodyActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
