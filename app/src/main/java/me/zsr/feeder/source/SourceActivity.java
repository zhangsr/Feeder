package me.zsr.feeder.source;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;

import com.avos.avoscloud.AVAnalytics;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.BuildConfig;
import me.zsr.feeder.R;
import me.zsr.feeder.base.BaseActivity;
import me.zsr.feeder.util.CommonEvent;
import me.zsr.feeder.util.SnackbarUtil;
import me.zsr.library_common.FileUtil;

public class SourceActivity extends BaseActivity implements OnSourceSelectedListener {
    private static final String SP_KEY_VERSION_CODE = "sp_key_version_code";
    private static final int MSG_DOUBLE_TAP = 0;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());
        setContentView(R.layout.activity_source);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getInt(SP_KEY_VERSION_CODE, 0) < BuildConfig.VERSION_CODE) {
            // Show newest version info
            // TODO: 11/4/15 Add en version info
            SnackbarUtil.show(this, "升级成功：" + FileUtil.readAssetFie(this, "version_info"));
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
        showItemList(App.SOURCE_ID_ALL);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    private void initToolbar() {
         mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Make arrow color white
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_hamberger);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void initDrawer() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.drawer_frame, SourceListFragment.getInstance());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void setListener() {
        mToolbar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                if (mHandler.hasMessages(MSG_DOUBLE_TAP)) {
                    mHandler.removeMessages(MSG_DOUBLE_TAP);
                    EventBus.getDefault().post(CommonEvent.SOURCE_TOOLBAR_DOUBLE_CLICK);
                } else {
                    mHandler.sendEmptyMessageDelayed(MSG_DOUBLE_TAP, ViewConfiguration.getDoubleTapTimeout());
                }
                break;
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_source, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                SourceHelper.showAddSourceDialog(this, mHandler);
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

    @Override
    public void onSourceSelected(long sourceId) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        showItemList(sourceId);
    }

    private void showItemList(long sourceId) {
        ItemListFragment fragment = (ItemListFragment) getFragmentManager().findFragmentById(R.id.details_frame);
        if (fragment == null || fragment.getShownSourceId() != sourceId) {
            fragment = ItemListFragment.newInstance(sourceId);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.details_frame, fragment);
            ft.commit();
        }
    }
}
