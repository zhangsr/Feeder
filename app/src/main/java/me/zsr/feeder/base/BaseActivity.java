package me.zsr.feeder.base;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.avos.avoscloud.AVAnalytics;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import me.zsr.feeder.BuildConfig;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedDB;
import me.zsr.feeder.util.LogUtil;

/**
 * @description:
 * @author: Match
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
        if (BuildConfig.DEBUG) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                // Dump db
                for (FeedSource feedSource : FeedDB.getInstance().loadAll()) {
                    LogUtil.e("FeedSource=" + feedSource.toString());
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.main_grey_dark));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    protected void setTranslucentStatus(boolean on) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }
}
