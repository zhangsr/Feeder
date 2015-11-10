package me.zsr.feeder.util;

import android.os.Handler;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.List;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 15-7-20
 */
public class UrlUtil {

    public static void searchForTarget(final Handler handler, final String input, final OnSearchResultListener listener) {
        if (TextUtils.isEmpty(input) || listener == null) {
            return;
        }

        // Check if it is a validate url
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(input)) {
            listener.onFound(input, false, "");
            return;
        }
        String inputWithPrefix = "http://" + input;
        if (urlValidator.isValid(inputWithPrefix)) {
            listener.onFound(inputWithPrefix, false, "");
            return;
        }

        //TODO Use async task ?
        // TODO: 9/1/15 do search server in another place ?
        // TODO: 9/1/15 return FeedSource would be better ?
        new Thread(new Runnable() {
            @Override
            public void run() {
                AVQuery<AVObject> query = new AVQuery<>("FeedSource");
                query.setLimit(300);
                try {
                    List<AVObject> avObjectList = query.find();
                    String target = "";
                    String reTitle = "";
                    double max = 0.5;
                    for (AVObject avObject : avObjectList) {
                        double similarity = StringUtil.getJaroWinklerDistance(
                                avObject.getString("title"), input);
                        if (similarity > max) {
                            max = similarity;
                            target = avObject.getString("url");
                            reTitle = avObject.getString("title");
                        }
                    }

                    final String resultUrl = target;
                    final String resultTitle = reTitle;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(resultUrl)) {
                                listener.onFound(resultUrl, true, resultTitle);
                            } else {
                                listener.onNotFound();
                            }
                        }
                    });
                } catch (AVException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onNotFound();
                        }
                    });
                }
            }
        }).start();
    }

    public interface OnSearchResultListener {
        void onFound(String result, boolean isUploaded, String reTitle);
        void onNotFound();
    }
}
