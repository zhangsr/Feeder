package me.zsr.feeder.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.text.SimpleDateFormat;
import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.util.DateUtil;
import me.zsr.feeder.util.VolleySingleton;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 15-6-12
 */
public class FeedItemListAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private List<FeedItem> mFeedItemList;
    private LayoutInflater mLayoutInflater;
    private String mFaviconUrl;

    public FeedItemListAdapter(List<FeedItem> list, String faviconUrl) {
        mFeedItemList = list;
        mLayoutInflater = LayoutInflater.from(App.getInstance());
        mFaviconUrl = faviconUrl;
    }

    @Override
    public int getCount() {
        return mFeedItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFeedItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        FeedItem feedItem = mFeedItemList.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.feed_item_list_item, null);
            viewHolder.imageView = (NetworkImageView) convertView.findViewById(R.id.feed_item_list_item_img);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.feed_item_list_item_title_txt);
            viewHolder.descriptionTextView = (TextView) convertView.findViewById(R.id.feed_item_list_item_description_txt);
            viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.feed_item_list_item_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageUrl(mFaviconUrl, VolleySingleton.getInstance().getImageLoader());
        viewHolder.titleTextView.setText(feedItem.getTitle());
        if (feedItem.getRead() == true) {
            viewHolder.titleTextView.setAlpha(0.54f);
            viewHolder.titleTextView.setTextColor(App.getInstance().getResources().getColor(R.color.main_grey_light));
        } else {
            viewHolder.titleTextView.setAlpha(0.87f);
            viewHolder.titleTextView.setTextColor(App.getInstance().getResources().getColor(R.color.main_grey_dark));
        }

        viewHolder.descriptionTextView.setText(feedItem.getDescription());
        viewHolder.timeTextView.setText(DateUtil.formatTime(feedItem.getDate()));

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;
        FeedItem feedItem = mFeedItemList.get(position);
        if (convertView == null) {
            headerViewHolder = new HeaderViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.feed_item_list_headers, null);
            headerViewHolder.textView = (TextView) convertView.findViewById(R.id.feed_item_list_header_txt);
            convertView.setTag(headerViewHolder);
        } else {
            headerViewHolder = (HeaderViewHolder) convertView.getTag();
        }
        headerViewHolder.textView.setText(DateUtil.formatDate(feedItem.getDate()));
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DD");
        long id = Long.valueOf(simpleDateFormat.format(mFeedItemList.get(position).getDate()));
        return id;
    }

    public void notifyDataSetChanged(List<FeedItem> list) {
        mFeedItemList = list;
        notifyDataSetChanged();
    }

    @Deprecated
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        // Mark : Use notifyDataSetChanged(List<FeedItem>) instead
    }

    class HeaderViewHolder {
        TextView textView;
    }

    class ViewHolder {
        NetworkImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView timeTextView;
    }
}
