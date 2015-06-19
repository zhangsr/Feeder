package me.zsr.feeder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.util.FeedDBUtil;

public class FeedBodyActivity extends Activity {
    private FeedItem mFeedItem;
    private WebView mBodyWebView;

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
        mBodyWebView = (WebView) findViewById(R.id.feed_body_web);
        WebSettings webSettings = mBodyWebView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mBodyWebView.loadDataWithBaseURL("", mFeedItem.getDescription(), "text/html", "UTF-8", "");
    }

    private void setListener() {

    }
}
