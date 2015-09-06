package me.zsr.feeder.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.NetworkImageView;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.BuildConfig;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedDB;
import me.zsr.feeder.data.FeedNetwork;
import me.zsr.feeder.util.AnalysisEvent;
import me.zsr.feeder.util.CommonEvent;
import me.zsr.feeder.util.LogUtil;
import me.zsr.feeder.util.NetworkUtil;
import me.zsr.feeder.util.UrlUtil;
import me.zsr.feeder.util.VolleySingleton;
import me.zsr.library.FileUtil;

public class FeedSourceActivity extends BaseActivity {
    private static final String SP_KEY_VERSION_CODE = "sp_key_version_code";
    private ImageButton mAddFeedButton;
    private ListView mFeedListView;
    private List<FeedSource> mFeedSourceList = new ArrayList<>();
    private FeedAdapter mFeedAdapter;
    private SwipeRefreshLayout mPullRefreshLayout;
    private FeedTabToolBar mTabToolBar;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());
        setContentView(R.layout.activity_feed_source);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getInt(SP_KEY_VERSION_CODE, 0) < BuildConfig.VERSION_CODE) {
            // Show newest version info
            showTip("升级成功：" + FileUtil.readAssetFie(this, "version_info"));
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(SP_KEY_VERSION_CODE, BuildConfig.VERSION_CODE);
            editor.apply();
        }

        initData();
        initView();
        setListener();

        // Auto refresh while wifi is enabled
        if (NetworkUtil.isWifiEnabled(this)) {
            FeedNetwork.getInstance().refreshAll();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mTabToolBar.setMode(App.getInstance().mCurrentMode);
        mFeedSourceList = FeedDB.getInstance().loadAll();
        mFeedAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        mFeedSourceList = FeedDB.getInstance().loadAll();
    }

    private void initView() {
        mAddFeedButton = (ImageButton) findViewById(R.id.add_feed_btn);
        mFeedListView = (ListView) findViewById(R.id.feed_lv);
        mFeedAdapter = new FeedAdapter();
        mFeedListView.setAdapter(mFeedAdapter);
        mPullRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.feed_pull_to_refresh_layout);
        mTabToolBar = (FeedTabToolBar) findViewById(R.id.feed_source_toolbar);
    }

    private void setListener() {
        mAddFeedButton.setOnClickListener(this);
        mPullRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedNetwork.getInstance().refreshAll();
            }
        });
        mFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putLong(App.KEY_BUNDLE_SOURCE_ID, mFeedSourceList.get(position).getId());
                Intent intent = new Intent(FeedSourceActivity.this, FeedItemActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mFeedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final FeedSource feedSource = mFeedSourceList.get(position);
                List<CharSequence> menuList = new ArrayList<>();
                if (FeedDB.getInstance().countFeedItemByRead(feedSource.getId(), false) != 0) {
                    menuList.add(getString(R.string.mark_as_read));
                }
                menuList.add(getString(R.string.cancel_feed));
                new MaterialDialog.Builder(FeedSourceActivity.this)
                        .title(feedSource.getTitle())
                        .items(menuList.toArray(new CharSequence[menuList.size()]))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int i,
                                                    CharSequence charSequence) {
                                switch (i) {
                                    case 0:
                                        FeedDB.getInstance().markAllAsRead(feedSource.getId());
                                        break;
                                    case 1:
                                        FeedDB.getInstance().deleteSource(feedSource.getId());
                                        break;
                                }

                                mFeedSourceList = FeedDB.getInstance().loadAll();
                                mFeedAdapter.notifyDataSetChanged();
                            }
                        }).show();
                return true;
            }
        });
        mTabToolBar.setOnTabChangedListener(new FeedTabToolBar.OnTabChangedListener() {
            @Override
            public void onTabChanged(App.Mode mode) {
                App.getInstance().mCurrentMode = mode;
                mFeedAdapter.notifyDataSetChanged();
            }
        });
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

                        AVAnalytics.onEvent(FeedSourceActivity.this, AnalysisEvent.ADD_SOURCE, input);

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

    private class FeedAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFeedSourceList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFeedSourceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeedSource feedSource = mFeedSourceList.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(FeedSourceActivity.this).inflate(R.layout.feed_source_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (NetworkImageView) convertView.findViewById(R.id.main_list_item_img);
                viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.main_list_item_title_txt);
                viewHolder.numTextView = (TextView) convertView.findViewById(R.id.main_list_item_num_txt);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.imageView.setImageUrl(feedSource.getFavicon(), VolleySingleton.getInstance().getImageLoader());
            viewHolder.titleTextView.setText(feedSource.getTitle());
            switch (App.getInstance().mCurrentMode) {
                case STAR:
                    viewHolder.numTextView.setText("" + FeedDB.getInstance().countFeedItemByStar(
                            feedSource.getId(), true));
                    break;
                case UNREAD:
                    viewHolder.numTextView.setText("" + FeedDB.getInstance().countFeedItemByRead(
                            feedSource.getId(), false));
                    break;
                case ALL:
                    viewHolder.numTextView.setText("" + feedSource.getFeedItems().size());
                    break;
                default:
            }
            return convertView;
        }

        private class ViewHolder {
            NetworkImageView imageView;
            TextView titleTextView;
            TextView numTextView;
        }
    }

    public void onEventMainThread(CommonEvent commonEvent) {
        switch (commonEvent) {
            case FEED_DB_UPDATED:
                LogUtil.e("feed db updated");
                mPullRefreshLayout.setRefreshing(false);
                mFeedSourceList = FeedDB.getInstance().loadAll();
                mFeedAdapter.notifyDataSetChanged();
                break;
            default:
        }
    }
}
