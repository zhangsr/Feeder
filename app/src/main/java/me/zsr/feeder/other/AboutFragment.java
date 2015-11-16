package me.zsr.feeder.other;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.zsr.feeder.R;
import me.zsr.feeder.base.BaseFragment;

/**
 * @description:
 * @author: Match
 * @date: 10/28/15
 */
public class AboutFragment extends BaseFragment {
    private static AboutFragment sInstance;
    private View mRootView;

    public static AboutFragment getInstance() {
        if (sInstance == null) {
            sInstance = new AboutFragment();
        }
        return sInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_about, container, false);
        return mRootView;
    }
}
