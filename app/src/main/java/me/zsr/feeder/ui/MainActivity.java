package me.zsr.feeder.ui;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;

import me.zsr.feeder.BuildConfig;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedNetwork;
import me.zsr.feeder.util.AnalysisEvent;
import me.zsr.feeder.util.UrlUtil;
import me.zsr.library_common.FileUtil;

public class MainActivity extends BaseActivity {
    private static final String SP_KEY_VERSION_CODE = "sp_key_version_code";
    private DrawerLayout mDrawerLayout;

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

    private void initView() {
        initToolbar();
        initDrawer();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void setListener() {
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Make arrow color white
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_ab_drawer);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void initDrawer() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.drawer_frame, SourceFragment.getInstance());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                showAddFeedDialog();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
