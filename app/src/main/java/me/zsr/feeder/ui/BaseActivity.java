package me.zsr.feeder.ui;

import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.avos.avoscloud.AVAnalytics;

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
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Dump db
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
