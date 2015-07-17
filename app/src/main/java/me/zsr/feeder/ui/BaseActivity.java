package me.zsr.feeder.ui;

import android.app.Activity;
import android.view.KeyEvent;

/**
 * @description:
 * @author: Saul
 * @date: 15-7-5
 * @version: 1.0
 */
public class BaseActivity extends Activity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Dump db
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
