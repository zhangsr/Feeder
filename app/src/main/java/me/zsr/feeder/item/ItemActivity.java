package me.zsr.feeder.item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.base.BaseActivity;
import me.zsr.feeder.util.DateUtil;
import me.zsr.feeder.data.FeedDB;

public class ItemActivity extends BaseActivity implements OnMenuItemClickListener {
    private FeedItem mFeedItem;
    private HtmlTextView mContentTextView;
    private TextView mTitleTextView;
    private TextView mSourceTextView;
    private TextView mDateTextView;
    private TextView mTimeTextView;

    private FragmentManager mFragmentManager;
    private DialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        mFragmentManager = getSupportFragmentManager();

        initData();
        initView();
        setListener();
    }

    private void initData() {
        mFeedItem = FeedDB.getInstance().getFeedItemByTitle(
                getIntent().getExtras().getString(App.KEY_BUNDLE_ITEM_TITLE));
    }

    private void initView() {
        initToolbar();
        initMenuFragment();
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

    private void initMenuFragment() {
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            MenuParams menuParams = new MenuParams();
            menuParams.setActionBarSize(actionBarHeight);
            menuParams.setMenuObjects(createMenuObject());
            mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        } else {
            // TODO: 10/26/15
        }
    }

    private List<MenuObject> createMenuObject() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.ic_body_close);
        MenuObject wechat = new MenuObject("Share to Wechat");
        wechat.setResource(R.drawable.ic_body_wechat);
        MenuObject weibo = new MenuObject("Share to Weibo");
        weibo.setResource(R.drawable.ic_body_weibo);
        MenuObject moments = new MenuObject("Share to moments");
        moments.setResource(R.drawable.ic_body_moments);
        MenuObject star = new MenuObject("Star");
        star.setResource(R.drawable.ic_body_star);
        MenuObject test = new MenuObject("Test");
        test.setResource(R.drawable.ic_body_test);

        menuObjects.add(close);
        menuObjects.add(wechat);
        menuObjects.add(weibo);
        menuObjects.add(moments);
        menuObjects.add(star);
        menuObjects.add(test);

        return menuObjects;
    }

    private void setListener() {
    }

    @Override
    public void onClick(View v) {

    }

    private static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.close();
        return os.toByteArray();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        // FIXME: 10/26/15 position not connect to init
        switch (position) {
            case 0:
                break;
            case 1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WXWebpageObject webpageObject = new WXWebpageObject();
                        webpageObject.webpageUrl = mFeedItem.getLink();
                        WXMediaMessage msg = new WXMediaMessage(webpageObject);
                        msg.title = mFeedItem.getTitle();
                        msg.description = mFeedItem.getDescription();
                        try {
                            // FIXME: 11/10/15
                            if (mFeedItem.getFeedSource() != null) {
                                msg.thumbData = getBytes((new URL(mFeedItem.getFeedSource().getFavicon()))
                                        .openStream());
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                                msg.thumbData = os.toByteArray();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("webpage");
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        App.getWXAPI().sendReq(req);
                    }
                }).start();
                break;
//            case 4:
//                if (mFeedItem.getStar()) {
//                    SnackbarUtil.show(this, R.string.remove_star_mark);
//                } else {
//                    SnackbarUtil.show(this, R.string.add_star_mark);
//                }
//                mFeedItem.setStar(!mFeedItem.getStar());
//                FeedDB.getInstance().saveFeedItem(mFeedItem, mFeedItem.getFeedSourceId());
//                break;
            default:
                Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
        }
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
                if (mFragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(mFragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            super.onBackPressed();
        }
    }
}
