package me.zsr.feeder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.NetworkImageView;
import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.util.CommonEvent;
import me.zsr.feeder.util.FeedDBUtil;
import me.zsr.feeder.util.FeedNetworkUtil;
import me.zsr.feeder.util.LogUtil;
import me.zsr.feeder.util.VolleySingleton;

public class FeedSourceActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton mAddFeedButton;
    private ListView mFeedListView;
    private List<FeedSource> mFeedSourceList = new ArrayList<>();
    private FeedAdapter mFeedAdapter;
    private PullRefreshLayout mPullRefreshLayout;
    private FeedTabToolBar mTabToolBar;

    private CharSequence[] mMenuArray = {
            "标记为已读",
            "取消订阅",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_source);

        initData();
        initView();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        mTabToolBar.setMode(App.getInstance().mCurrentMode);
        mFeedAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        mFeedSourceList = FeedDBUtil.getInstance().loadAll();
    }

    private void initView() {
        mAddFeedButton = (ImageButton) findViewById(R.id.add_feed_btn);
        mFeedListView = (ListView) findViewById(R.id.feed_lv);
        mFeedAdapter = new FeedAdapter();
        mFeedListView.setAdapter(mFeedAdapter);
        mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.feed_pull_to_refresh_layout);
        mPullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        mTabToolBar = (FeedTabToolBar) findViewById(R.id.feed_source_toolbar);
    }

    private void setListener() {
        mAddFeedButton.setOnClickListener(this);
        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedNetworkUtil.fetchAll();
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
                new MaterialDialog.Builder(FeedSourceActivity.this)
                        .title(feedSource.getTitle())
                        .items(mMenuArray)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int i,
                                                    CharSequence charSequence) {
                                switch (i) {
                                    case 0:
                                        FeedDBUtil.getInstance().markAllAsRead(feedSource.getId());
                                        break;
                                    case 1:
                                        break;
                                }
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
                .alwaysCallInputCallback() // this forces the callback to be invoked with every input change
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        final String url = dialog.getInputEditText().getText().toString();
                        FeedNetworkUtil.verifyFeedSource(url, new FeedNetworkUtil.OnVerifyFeedListener() {
                                    @Override
                                    public void onResult(boolean isValid) {
                                        if (isValid) {
                                            FeedNetworkUtil.addFeedSource(url);
                                        } else {
                                            LogUtil.e("Source invalid");
                                            Toast.makeText(App.getInstance(), "Source invalid", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .input(R.string.abc_search_hint, 0, false, new MaterialDialog.InputCallback() {
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
                    viewHolder.numTextView.setText("" + FeedDBUtil.getInstance().countFeedItemByStar(
                            feedSource.getId(), true));
                    break;
                case UNREAD:
                    viewHolder.numTextView.setText("" + FeedDBUtil.getInstance().countFeedItemByRead(
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
                mPullRefreshLayout.setRefreshing(false);
                mFeedSourceList = FeedDBUtil.getInstance().loadAll();
                mFeedAdapter.notifyDataSetChanged();
                break;
            default:
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                // Test add source
//                FeedNetworkUtil.addFeedSource("http://www.coolshell.cn/feed");
                FeedNetworkUtil.addFeedSource("http://www.zhihu.com/rss");
                return true;
            default:
        }
        return super.onKeyDown(keyCode, event);
    }
}
