package me.zsr.feeder.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import me.zsr.feeder.App;
import me.zsr.feeder.R;

/**
 * @description:
 * @author: Saul
 * @date: 15-7-19
 * @version: 1.0
 */
public class FeedTabToolBar extends FrameLayout implements View.OnClickListener {
    private ImageButton mStarButton;
    private ImageButton mUnreadButton;
    private ImageButton mAllButton;
    private OnTabChangedListener mTabChangedListener;
    private App.Mode mCurrentMode = App.Mode.UNREAD;

    public FeedTabToolBar(Context context) {
        this(context, null);
    }

    public FeedTabToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.feed_bottom_tool_bar, null);
        mStarButton = (ImageButton) view.findViewById(R.id.star_btn);
        mUnreadButton = (ImageButton) view.findViewById(R.id.unread_btn);
        mUnreadButton.setImageResource(R.drawable.ic_brightness_1_white_24dp);
        mAllButton = (ImageButton) view.findViewById(R.id.all_btn);

        mStarButton.setOnClickListener(this);
        mUnreadButton.setOnClickListener(this);
        mAllButton.setOnClickListener(this);

        addView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.star_btn:
                setMode(App.Mode.STAR);
                break;
            case R.id.unread_btn:
                setMode(App.Mode.UNREAD);
                break;
            case R.id.all_btn:
                setMode(App.Mode.ALL);
                break;
            default:
        }
    }

    public interface OnTabChangedListener {
        void onTabChanged(App.Mode mode);
    }

    public void setOnTabChangedListener(OnTabChangedListener listener) {
        mTabChangedListener = listener;
    }

    public void setMode(App.Mode mode) {
        if (mCurrentMode != mode) {
            switch (mode) {
                case STAR:
                    mStarButton.setImageResource(R.drawable.ic_grade_white_18dp);
                    mUnreadButton.setImageResource(R.drawable.ic_brightness_1_grey600_24dp);
                    mAllButton.setImageResource(R.drawable.ic_subject_grey600_18dp);

                    break;
                case UNREAD:
                    mStarButton.setImageResource(R.drawable.ic_grade_grey600_18dp);
                    mUnreadButton.setImageResource(R.drawable.ic_brightness_1_white_24dp);
                    mAllButton.setImageResource(R.drawable.ic_subject_grey600_18dp);
                    break;
                case ALL:
                    mStarButton.setImageResource(R.drawable.ic_grade_grey600_18dp);
                    mUnreadButton.setImageResource(R.drawable.ic_brightness_1_grey600_24dp);
                    mAllButton.setImageResource(R.drawable.ic_subject_white_18dp);
                    break;
                default:
            }

            mCurrentMode = mode;
            if (mTabChangedListener != null) {
                mTabChangedListener.onTabChanged(mode);
            }
        }
    }
}
