package me.zsr.feeder.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import me.zsr.feeder.R;

public class MainActivity extends Activity implements View.OnClickListener {
    private ImageButton mAddFeedButton;
    private ListView mFeedListView;
    private ImageButton mFavorButton;
    private ImageButton mUnreadButton;
    private ImageButton mAllButton;
    private List<String> mTestFeedList = new ArrayList<>();
    private FeedAdapter mFeedAdapter;
    private PullRefreshLayout mPullRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        setListener();
    }

    private void initData() {
        mTestFeedList.add("知乎每日精选");
        mTestFeedList.add("干货集中营");
        mTestFeedList.add("开发者头条");
        mTestFeedList.add("Matrix67");
    }

    private void initView() {
        mAddFeedButton = (ImageButton) findViewById(R.id.add_feed_btn);
        mFeedListView = (ListView) findViewById(R.id.feed_lv);
        mFeedAdapter = new FeedAdapter();
        mFeedListView.setAdapter(mFeedAdapter);
        mFavorButton = (ImageButton) findViewById(R.id.favor_btn);
        mUnreadButton = (ImageButton) findViewById(R.id.unread_btn);
        mUnreadButton.setImageResource(R.drawable.ic_brightness_1_white_24dp);
        mAllButton = (ImageButton) findViewById(R.id.all_btn);
        mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.feed_pull_to_refresh_layout);
        mPullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
    }

    private void setListener() {
        mAddFeedButton.setOnClickListener(this);
        mFavorButton.setOnClickListener(this);
        mUnreadButton.setOnClickListener(this);
        mAllButton.setOnClickListener(this);
        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_feed_btn:
                startActivity(new Intent(MainActivity.this, AddSourceActivity.class));
                break;
            case R.id.favor_btn:
                // Refresh UI
                mFavorButton.setImageResource(R.drawable.ic_grade_white_18dp);
                mUnreadButton.setImageResource(R.drawable.ic_brightness_1_grey600_24dp);
                mAllButton.setImageResource(R.drawable.ic_subject_grey600_18dp);

                // Do things
                break;
            case R.id.unread_btn:
                // Refresh UI
                mFavorButton.setImageResource(R.drawable.ic_grade_grey600_18dp);
                mUnreadButton.setImageResource(R.drawable.ic_brightness_1_white_24dp);
                mAllButton.setImageResource(R.drawable.ic_subject_grey600_18dp);

                // Do things
                break;
            case R.id.all_btn:
                // Refresh UI
                mFavorButton.setImageResource(R.drawable.ic_grade_grey600_18dp);
                mUnreadButton.setImageResource(R.drawable.ic_brightness_1_grey600_24dp);
                mAllButton.setImageResource(R.drawable.ic_subject_white_18dp);

                // Do things
                break;
            default:
        }
    }

    private class FeedAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTestFeedList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTestFeedList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.main_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.main_list_item_img);
                viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.main_list_item_title_txt);
                viewHolder.numTextView = (TextView) convertView.findViewById(R.id.main_list_item_num_txt);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.titleTextView.setText(mTestFeedList.get(position));
            viewHolder.numTextView.setText("" + (int) (Math.random() * 1000));
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView titleTextView;
            TextView numTextView;
        }
    }
}
