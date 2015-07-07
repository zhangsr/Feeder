package me.zsr.feeder.ui;

import android.app.Activity;
import android.os.Bundle;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.util.FeedDBUtil;

public class FeedBodyActivity extends Activity {
    private FeedItem mFeedItem;
    private HtmlTextView mBodyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_body);

        initData();
        initView();
        setListener();
    }

    private void initData() {
        mFeedItem = FeedDBUtil.getInstance().getFeedItemById(
                getIntent().getExtras().getLong(App.KEY_BUNDLE_ITEM_ID));
    }

    private void initView() {
        mBodyTextView = (HtmlTextView) findViewById(R.id.feed_body_txt);
        mBodyTextView.setHtmlFromString(mFeedItem.getDescription(), false);
    }

    private void setListener() {

    }
}
