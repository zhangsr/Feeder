package me.zsr.feeder.item;

import android.graphics.Bitmap;

/**
 * @description:
 * @author: Match
 * @date: 11/16/15
 */
public interface IItemPresenter {

    void loadItem(String itemTitle);

    void shareToWechat(Bitmap defaultImage);

    void shareToMoment(Bitmap defaultImage);

    void shareToWeibo();

    void shareToInstapaper();

    void shareToGooglePlus();

    void shareToPocket();
}
