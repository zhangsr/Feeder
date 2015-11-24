package me.zsr.feeder.item;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

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
    private static final int MSG_DOUBLE_TAP = 0;
    private HtmlTextView mContentTextView;
    private TextView mTitleTextView;
    private TextView mSourceTextView;
    private TextView mDateTextView;
    private TextView mTimeTextView;
    private NestedScrollView mScrollView;

    private IItemPresenter mPresenter;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        initView();

        mPresenter = new ItemPresenter(this, this);
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
        mScrollView = (NestedScrollView) findViewById(R.id.scroll_layout);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnClickListener(this);

        // Make arrow color white
        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                if (mHandler.hasMessages(MSG_DOUBLE_TAP)) {
                    mHandler.removeMessages(MSG_DOUBLE_TAP);
                    mScrollView.smoothScrollTo(0, 0);
                } else {
                    mHandler.sendEmptyMessageDelayed(MSG_DOUBLE_TAP, ViewConfiguration.getDoubleTapTimeout());
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                showShareMenu();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showShareMenu() {
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(this);
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.wechat)
                .icon(R.drawable.ic_menu_wechat)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.moment)
                .icon(R.drawable.ic_menu_moment)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.weibo)
                .icon(R.drawable.ic_menu_weibo)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.instapaper)
                .icon(R.drawable.ic_menu_instapaper)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.google_plus)
                .icon(R.drawable.ic_menu_google_plus)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.pocket)
                .icon(R.drawable.ic_menu_pocket)
                .build());

        new MaterialDialog.Builder(this)
                .title(R.string.share_to)
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                mPresenter.shareToWechat(BitmapFactory.decodeResource(
                                        getResources(), R.drawable.ic_launcher));
                                break;
                            case 1:
                                mPresenter.shareToMoment(BitmapFactory.decodeResource(
                                        getResources(), R.drawable.ic_launcher));
                                break;
                            case 2:
                                mPresenter.shareToWeibo();
                                break;
                            case 3:
                                mPresenter.shareToInstapaper();
                                break;
                            case 4:
                                mPresenter.shareToGooglePlus();
                                break;
                            case 5:
                                mPresenter.shareToPocket();
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .show();
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

    @Override
    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
