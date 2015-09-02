package me.zsr.feeder.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.util.DateUtil;
import me.zsr.feeder.data.FeedDB;

public class FeedBodyActivity extends BaseActivity {
    private FeedItem mFeedItem;
    private HtmlTextView mContentTextView;
    private TextView mTitleTextView;
    private TextView mSourceTextView;
    private TextView mDateTextView;
    private TextView mTimeTextView;
    private ImageButton mStarButton;
    private ImageButton mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_body);

        initData();
        initView();
        setListener();
    }

    private void initData() {
        mFeedItem = FeedDB.getInstance().getFeedItemById(
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
        mContentTextView.setHtmlText(mFeedItem.getContent());
        mStarButton = (ImageButton) findViewById(R.id.feed_body_star_btn);
        mShareButton = (ImageButton) findViewById(R.id.feed_body_share_btn);
    }

    private void setListener() {
        mStarButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feed_body_star_btn:
                if (mFeedItem.getStar()) {
                    showTip("已取消星标");
                } else {
                    showTip("已添加星标");
                }
                mFeedItem.setStar(!mFeedItem.getStar());
                FeedDB.getInstance().saveFeedItem(mFeedItem, mFeedItem.getFeedSourceId());
                break;
            case R.id.feed_body_share_btn:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WXWebpageObject webpageObject = new WXWebpageObject();
                        webpageObject.webpageUrl = mFeedItem.getLink();
                        WXMediaMessage msg = new WXMediaMessage(webpageObject);
                        msg.title = mFeedItem.getTitle();
                        msg.description = mFeedItem.getDescription();
                        try {
                            msg.thumbData = getBytes((new URL(mFeedItem.getFeedSource().getFavicon()))
                                    .openStream());
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
            default:
        }
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
}
