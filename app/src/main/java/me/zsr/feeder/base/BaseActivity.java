package me.zsr.feeder.base;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;

import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedDB;
import me.zsr.feeder.util.LogUtil;

/**
 * @description:
 * @author: Saul
 * @date: 15-7-5
 * @version: 1.0
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Dump db
            for (FeedSource feedSource : FeedDB.getInstance().loadAll()) {
                LogUtil.e("FeedSource=" + feedSource.toString());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showTip(int msgRes) {
        final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), msgRes, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.got_it, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    protected void showTip(String msg) {
        final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.got_it, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
