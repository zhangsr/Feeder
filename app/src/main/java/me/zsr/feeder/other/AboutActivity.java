package me.zsr.feeder.other;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zsr.feeder.BuildConfig;
import me.zsr.feeder.R;
import me.zsr.feeder.base.BaseActivity;

public class AboutActivity extends BaseActivity {
    private static final String URL_GITHUB = "https://github.com/zhangsr/Feeder";
    private static final String URL_GOOGLE_PLUS = "https://plus.google.com/108838785221141135915";
    private static final String URL_BUG = "https://github.com/zhangsr/Feeder/issues";
    private static final String URL_STORE = "http://www.wandoujia.com/apps/me.zsr.feeder";
    private static final String URL_AARON_SWARTZ = "https://en.wikipedia.org/wiki/Aaron_Swartz";
    @Bind(R.id.version_name_txt)
    TextView mVersionNameTextView;
    @Bind(R.id.info_img)
    ImageView mInfoImageView;
    @Bind(R.id.change_log_img)
    ImageView mChangelogImageView;
    @Bind(R.id.author_img)
    ImageView mAuthorImageView;
    @Bind(R.id.google_plus_img)
    ImageView mGooglePlusImageView;
    @Bind(R.id.github_img)
    ImageView mGithubImageView;
    @Bind(R.id.web_img)
    ImageView mWebImageView;
    @Bind(R.id.bug_img)
    ImageView mBugImageView;
    @Bind(R.id.store_img)
    ImageView mStoreImageView;
    @Bind(R.id.wechat_img)
    ImageView mWechatImageView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Make arrow color white
        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mVersionNameTextView.setText(BuildConfig.VERSION_NAME);
        mInfoImageView.setColorFilter(getResources().getColor(R.color.main_grey_light));
        mChangelogImageView.setColorFilter(getResources().getColor(R.color.main_grey_light));
        mAuthorImageView.setColorFilter(getResources().getColor(R.color.main_grey_light));
        mGooglePlusImageView.setColorFilter(getResources().getColor(R.color.main_grey_light));
        mGithubImageView.setColorFilter(getResources().getColor(R.color.main_grey_light));
        mWebImageView.setColorFilter(getResources().getColor(R.color.main_grey_light));
        mBugImageView.setColorFilter(getResources().getColor(R.color.main_grey_light));
        mStoreImageView.setColorFilter(getResources().getColor(R.color.main_grey_light));
        mWechatImageView.setColorFilter(getResources().getColor(R.color.main_grey_light));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openUrl(String url) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
    }

    @OnClick({
            R.id.google_plus_layout,
            R.id.github_layout,
            R.id.bug_layout,
            R.id.store_layout,
            R.id.aaron_swartz_layout,
    })
    public void layoutOnClick(View view) {
        switch (view.getId()) {
            case R.id.google_plus_layout:
                openUrl(URL_GOOGLE_PLUS);
                break;
            case R.id.github_layout:
                openUrl(URL_GITHUB);
                break;
            case R.id.bug_layout:
                openUrl(URL_BUG);
                break;
            case R.id.store_layout:
                openUrl(URL_STORE);
                break;
            case R.id.aaron_swartz_layout:
                openUrl(URL_AARON_SWARTZ);
                break;
        }
    }
}
