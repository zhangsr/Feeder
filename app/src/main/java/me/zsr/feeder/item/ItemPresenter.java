package me.zsr.feeder.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.instapaper.Instapaper;
import cn.sharesdk.pocket.Pocket;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.DataModel;
import me.zsr.feeder.data.IDataModel;

/**
 * @description:
 * @author: Match
 * @date: 11/16/15
 */
public class ItemPresenter implements IItemPresenter {
    private IItemView mView;
    private IDataModel mModel;
    private FeedItem mFeedItem;
    private Context mContext;
    private PlatformActionListener mPlatformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            mView.showMsg(mContext.getString(R.string.share_complete));
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            mView.showMsg(mContext.getString(R.string.share_failed));
        }

        @Override
        public void onCancel(Platform platform, int i) {
            mView.showMsg(mContext.getString(R.string.share_cancel));
        }
    };

    public ItemPresenter(IItemView view, Context context) {
        mView = view;
        mModel = new DataModel();
        mContext = context;
    }

    @Override
    public void loadItem(final String itemTitle) {
        mModel.loadItem(itemTitle, new OnItemLoadListener() {
            @Override
            public void success(FeedItem item) {
                mFeedItem = item;
                mView.updated(item);
            }

            @Override
            public void error(String msg) {
                mView.showError(msg);
            }
        });
    }

    @Override
    public void shareToWechat(Bitmap defaultImage) {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setShareType(Wechat.SHARE_WEBPAGE);
        sp.setTitle(mFeedItem.getTitle());
        sp.setText(mFeedItem.getDescription());
        sp.setUrl(mFeedItem.getLink());
        FeedSource feedSource = mFeedItem.getFeedSource();
        if (feedSource != null
                && !TextUtils.isEmpty(feedSource.getFavicon())) {
            sp.setImageUrl(feedSource.getFavicon());
        } else if (defaultImage != null) {
            sp.setImageData(defaultImage);
        }

        Platform platform = ShareSDK.getPlatform(Wechat.NAME);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(sp);
    }

    @Override
    public void shareToMoment(Bitmap defaultImage) {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setShareType(Wechat.SHARE_WEBPAGE);
        sp.setTitle(mFeedItem.getTitle());
        sp.setText(mFeedItem.getDescription());
        sp.setUrl(mFeedItem.getLink());
        FeedSource feedSource = mFeedItem.getFeedSource();
        if (feedSource != null
                && !TextUtils.isEmpty(feedSource.getFavicon())) {
            sp.setImageUrl(feedSource.getFavicon());
        } else if (defaultImage != null) {
            sp.setImageData(defaultImage);
        }

        Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(sp);
    }

    @Override
    public void shareToWeibo() {
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setText(getPureTextShare());

        Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(sp);
    }

    @Override
    public void shareToInstapaper() {
        Instapaper.ShareParams sp = new Instapaper.ShareParams();
        sp.setTitle(mFeedItem.getTitle());
        sp.setText(mFeedItem.getDescription());
        sp.setUrl(mFeedItem.getLink());

        Platform platform = ShareSDK.getPlatform(Instapaper.NAME);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(sp);
    }

    @Override
    public void shareToGooglePlus() {
        GooglePlus.ShareParams sp = new GooglePlus.ShareParams();
        sp.setText(getPureTextShare());

        Platform platform = ShareSDK.getPlatform(GooglePlus.NAME);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(sp);
    }

    @Override
    public void shareToPocket() {
        Pocket.ShareParams sp = new Pocket.ShareParams();
        sp.setTitle(mFeedItem.getTitle());
        sp.setUrl(mFeedItem.getLink());

        Platform platform = ShareSDK.getPlatform(Pocket.NAME);
        platform.setPlatformActionListener(mPlatformActionListener);
        platform.share(sp);
    }

    private String getPureTextShare() {
        String shareText = mFeedItem.getTitle()
                + " "
                + mFeedItem.getLink()
                + " Shared by Feeder app";
        return shareText;
    }
}
