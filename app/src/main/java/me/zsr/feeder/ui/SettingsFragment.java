package me.zsr.feeder.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.zsr.feeder.R;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 10/28/15
 */
public class SettingsFragment extends FragmentBase {
    private static SettingsFragment sInstance;
    private View mRootView;

    public static SettingsFragment getInstance() {
        if (sInstance == null) {
            sInstance = new SettingsFragment();
        }
        return sInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return mRootView;
    }
}
