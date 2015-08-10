package me.zsr.feeder.ui;

import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.avos.avoscloud.AVAnalytics;

import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.util.FeedDBUtil;
import me.zsr.feeder.util.LogUtil;

/**
 * @description:
 * @author: Saul
 * @date: 15-7-5
 * @version: 1.0
 */
public class BaseActivity extends AppCompatActivity {

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
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            // Dump db
//            for (FeedSource feedSource : FeedDBUtil.getInstance().loadAll()) {
//                LogUtil.e("FeedSource=" + feedSource.toString());
//            }
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }
}
