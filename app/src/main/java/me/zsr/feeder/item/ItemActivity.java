package me.zsr.feeder.item;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

/**
 * @description:
 * @author: Match
 * @date: 8/29/15
 */
public class ItemActivity extends BaseActivity implements IItemView {
    private HtmlTextView mContentTextView;
    private TextView mTitleTextView;
    private TextView mSourceTextView;
    private TextView mDateTextView;
    private TextView mTimeTextView;

    private IItemPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        initView();

        mPresenter = new ItemPresenter(this);
        String itemTitle = getIntent().getExtras().getString(App.KEY_BUNDLE_ITEM_TITLE);
        if (TextUtils.isEmpty(itemTitle)) {
            showError("Item Title is empty");
        } else {
            mPresenter.loadItem(itemTitle);
        }
    }

    private void initView() {
        initToolbar();
        mTitleTextView = (TextView) findViewById(R.id.feed_body_title);
        mDateTextView = (TextView) findViewById(R.id.feed_body_date);
        mTimeTextView = (TextView) findViewById(R.id.feed_body_time);
        mSourceTextView = (TextView) findViewById(R.id.feed_body_source);
        mContentTextView = (HtmlTextView) findViewById(R.id.feed_body_content);
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

    @Override
    public void updated(FeedItem item) {
        mTitleTextView.setText(item.getTitle());
        mDateTextView.setText(DateUtil.formatDate(item.getDate()));
        mTimeTextView.setText(DateUtil.formatTime(item.getDate()));
        // FIXME: 11/9/15 Why FeedSource become null
        if (item.getFeedSource() != null) {
            mSourceTextView.setText(item.getFeedSource().getTitle());
        }
        mContentTextView.setHtmlText(item.getContent());
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
