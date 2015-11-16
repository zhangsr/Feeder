package me.zsr.feeder.item;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.base.BaseActivity;
import me.zsr.feeder.util.DateUtil;
import me.zsr.feeder.data.FeedDB;

/**
 * @description:
 * @author: Match
 * @date: 8/29/15
 */
public class ItemActivity extends BaseActivity {
    private FeedItem mFeedItem;
    private HtmlTextView mContentTextView;
    private TextView mTitleTextView;
    private TextView mSourceTextView;
    private TextView mDateTextView;
    private TextView mTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        initData();
        initView();
    }

    private void initData() {
        mFeedItem = FeedDB.getInstance().getFeedItemByTitle(
                getIntent().getExtras().getString(App.KEY_BUNDLE_ITEM_TITLE));
    }

    private void initView() {
        initToolbar();
        mTitleTextView = (TextView) findViewById(R.id.feed_body_title);
        mTitleTextView.setText(mFeedItem.getTitle());
        mDateTextView = (TextView) findViewById(R.id.feed_body_date);
        mDateTextView.setText(DateUtil.formatDate(mFeedItem.getDate()));
        mTimeTextView = (TextView) findViewById(R.id.feed_body_time);
        mTimeTextView.setText(DateUtil.formatTime(mFeedItem.getDate()));
        mSourceTextView = (TextView) findViewById(R.id.feed_body_source);
        // FIXME: 11/9/15 Why FeedSource become null
        if (mFeedItem.getFeedSource() != null) {
            mSourceTextView.setText(mFeedItem.getFeedSource().getTitle());
        }
        mContentTextView = (HtmlTextView) findViewById(R.id.feed_body_content);
        mContentTextView.setHtmlText(mFeedItem.getContent());
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Make arrow color white
        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
