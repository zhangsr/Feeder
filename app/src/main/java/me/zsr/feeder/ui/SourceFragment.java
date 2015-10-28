package me.zsr.feeder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedDB;
import me.zsr.feeder.data.FeedNetwork;
import me.zsr.feeder.util.CommonEvent;
import me.zsr.feeder.util.LogUtil;
import me.zsr.feeder.util.NetworkUtil;
import me.zsr.feeder.util.VolleySingleton;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 10/28/15
 */
public class SourceFragment extends FragmentBase {
    private static SourceFragment sInstance;
    private List<FeedSource> mFeedSourceList = new ArrayList<>();

    private View mRootView;
    private ListView mFeedListView;
    private FeedAdapter mFeedAdapter;
    private SwipeRefreshLayout mPullRefreshLayout;
    private FeedTabToolBar mTabToolBar;

    public static SourceFragment getInstance() {
        if (sInstance == null) {
            sInstance = new SourceFragment();
        }
        return sInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_source, container, false);
        initData();
        initView();
        setListener();

        // Auto refresh while wifi is enabled
        if (NetworkUtil.isWifiEnabled(getActivity())) {
            FeedNetwork.getInstance().refreshAll();
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        mFeedSourceList = FeedDB.getInstance().loadAll();
    }

    private void initView() {
        mFeedListView = (ListView) mRootView.findViewById(R.id.feed_lv);
        mFeedAdapter = new FeedAdapter();
        mFeedListView.setAdapter(mFeedAdapter);
        mPullRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.feed_pull_to_refresh_layout);
        mTabToolBar = (FeedTabToolBar) mRootView.findViewById(R.id.feed_source_toolbar);
        mTabToolBar.setMode(App.getInstance().mCurrentMode);
    }

    private void setListener() {
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
                Intent intent = new Intent(getActivity(), FeedItemActivity.class);
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
                menuList.add(getString(R.string.remove_subscription));
                new MaterialDialog.Builder(getActivity())
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.feed_source_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (NetworkImageView) convertView.findViewById(R.id.main_list_item_img);
                viewHolder.imageView.setErrorImageResId(R.drawable.ic_rss);
                viewHolder.imageView.setDefaultImageResId(R.drawable.ic_rss);
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
