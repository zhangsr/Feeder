package me.zsr.feeder.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import me.zsr.feeder.R;

/**
 * @description:
 * @author: Match
 * @date: 11/5/15
 */
public class SnackbarUtil {
    public static void show(Activity activity, int msgRes) {
        final Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView(), msgRes, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.got_it, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public static void show(Activity activity, String msg) {
        final Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView(), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.got_it, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
