package me.zsr.feeder.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import me.zsr.feeder.BuildConfig;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedNetwork;
import me.zsr.feeder.util.AnalysisEvent;
import me.zsr.feeder.util.UrlUtil;
import me.zsr.library_common.FileUtil;

public class MainActivity extends BaseActivity {
    private static final String SP_KEY_VERSION_CODE = "sp_key_version_code";
    private ImageView mAddFeedButton;
    private Toolbar mTopToolbar;
    private FrameLayout mRootView;
    private View mHamburgerView;
    private LinearLayout mSourceMenuItem;
    private LinearLayout mAboutMenuItem;
    private LinearLayout mSettingsMenuItem;
    private GuillotineAnimation mGuillotineAnimation;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getInt(SP_KEY_VERSION_CODE, 0) < BuildConfig.VERSION_CODE) {
            // Show newest version info
            showTip("升级成功：" + FileUtil.readAssetFie(this, "version_info"));
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(SP_KEY_VERSION_CODE, BuildConfig.VERSION_CODE);
            editor.apply();
        }

        initView();
        setListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // TODO: 10/28/15 refresh SourceFragment
//        mTabToolBar.setMode(App.getInstance().mCurrentMode);
//        mFeedSourceList = FeedDB.getInstance().loadAll();
//        mFeedAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mAddFeedButton = (ImageView) findViewById(R.id.add_feed_btn);
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRootView = (FrameLayout) findViewById(R.id.root);
        mHamburgerView = findViewById(R.id.content_hamburger);

        if (mTopToolbar != null) {
            setSupportActionBar(mTopToolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.menu_guillotine, null);
        mSourceMenuItem = (LinearLayout) guillotineMenu.findViewById(R.id.source_group);
        mAboutMenuItem = (LinearLayout) guillotineMenu.findViewById(R.id.about_group);
        mSettingsMenuItem = (LinearLayout) guillotineMenu.findViewById(R.id.settings_group);
        mRootView.addView(guillotineMenu);

        mGuillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu,
                guillotineMenu.findViewById(R.id.guillotine_hamburger), mHamburgerView)
                .setActionBarViewForAnimation(mTopToolbar)
                .setClosedOnStart(true)
                .build();

        switchDetailFragment(SourceFragment.getInstance());
    }

    private void setListener() {
        mAddFeedButton.setOnClickListener(this);
        mSourceMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDetailFragment(SourceFragment.getInstance());
                mGuillotineAnimation.close();
            }
        });
        mAboutMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDetailFragment(AboutFragment.getInstance());
                mGuillotineAnimation.close();
            }
        });
        mSettingsMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDetailFragment(SettingsFragment.getInstance());
                mGuillotineAnimation.close();
            }
        });
    }

    private void switchDetailFragment(Fragment detailFragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_detail_framelayout, detailFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_feed_btn:
                showAddFeedDialog();
                break;
            default:
        }
    }

    private void showAddFeedDialog() {
        new MaterialDialog.Builder(this)
                .title("添加订阅")
                .content("RSS地址、网址或名称")
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText("添加")
                .negativeText("取消")
                .autoDismiss(false)
                .alwaysCallInputCallback() // this forces the callback to be invoked with every input change
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        super.onPositive(dialog);
                        String input = dialog.getInputEditText().getText().toString();

                        AVAnalytics.onEvent(MainActivity.this, AnalysisEvent.ADD_SOURCE, input);

                        UrlUtil.searchForTarget(mHandler, input, new UrlUtil.OnSearchResultListener() {
                            @Override
                            public void onFound(final String result, boolean isUploaded, String reTitle) {
                                if (isUploaded) {
                                    FeedNetwork.getInstance().addSource(result, reTitle, new FeedNetwork.OnAddListener() {
                                        @Override
                                        public void onError(String msg) {
                                            showError(msg);
                                        }
                                    });
                                    dialog.dismiss();
                                } else {
                                    FeedNetwork.getInstance().verifySource(result, new FeedNetwork.OnVerifyListener() {
                                        @Override
                                        public void onResult(boolean isValid, FeedSource feedSource) {
                                            if (isValid) {
                                                // Upload
                                                AVObject feedSourceObj = new AVObject("FeedSource");
                                                feedSourceObj.put("title", feedSource.getTitle());
                                                feedSourceObj.put("url", feedSource.getUrl());
                                                feedSourceObj.put("link", feedSource.getLink());
                                                feedSourceObj.saveInBackground();

                                                FeedNetwork.getInstance().addSource(feedSource, new FeedNetwork.OnAddListener() {
                                                    @Override
                                                    public void onError(String msg) {
                                                        showError(msg);
                                                    }
                                                });
                                                dialog.dismiss();
                                            } else {
                                                showError("无效的源");
                                                //TODO Add suffix and try again
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onNotFound() {
                                showError("没有找到相关的源");
                            }
                        });
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
                .input("例如输入：酷", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                }).show();
    }
}
