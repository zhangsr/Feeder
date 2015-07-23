package me.zsr.feeder.util;

import android.text.TextUtils;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 15-7-20
 */
public class UrlUtil {

    //TODO may be a async method
    public static void searchForTarget(String input, OnSearchResultListener listener) {
        if (TextUtils.isEmpty(input) || listener == null) {
            return;
        }

        // Check if it is a validate url
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(input)) {
            listener.onFound(input);
        }
        String inputWithPrefix = "http://" + input;
        if (urlValidator.isValid(inputWithPrefix)) {
            listener.onFound(inputWithPrefix);
        }

        //TODO Search in server

        listener.onNotFound();
    }

    public interface OnSearchResultListener {
        void onFound(String result);
        void onNotFound();
    }
}
