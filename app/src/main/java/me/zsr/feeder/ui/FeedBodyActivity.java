package me.zsr.feeder.ui;

import android.os.Bundle;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.util.DateUtil;
import me.zsr.feeder.util.FeedDBUtil;

public class FeedBodyActivity extends BaseActivity {
    private FeedItem mFeedItem;
    private HtmlTextView mContentTextView;
    private TextView mTitleTextView;
    private TextView mSourceTextView;
    private TextView mDateTextView;
    private TextView mTimeTextView;

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
        mTitleTextView = (TextView) findViewById(R.id.feed_body_title);
        mTitleTextView.setText(mFeedItem.getTitle());
        mDateTextView = (TextView) findViewById(R.id.feed_body_date);
        mDateTextView.setText(DateUtil.formatDate(mFeedItem.getDate()));
        mTimeTextView = (TextView) findViewById(R.id.feed_body_time);
        mTimeTextView.setText(DateUtil.formatTime(mFeedItem.getDate()));
        mSourceTextView = (TextView) findViewById(R.id.feed_body_source);
        mSourceTextView.setText(mFeedItem.getFeedSource().getTitle());
        mContentTextView = (HtmlTextView) findViewById(R.id.feed_body_content);
        mContentTextView.setHtmlText(mFeedItem.getDescription());
    }

    private void setListener() {

    }
}
