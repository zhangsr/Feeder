package me.zsr.feeder.source;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.text.SimpleDateFormat;
import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.base.BaseListAdapter;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.util.DateUtil;
import me.zsr.feeder.util.VolleySingleton;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * @description:
 * @author: Match
 * @date: 15-6-12
 */
public class ItemListAdapter extends BaseListAdapter implements StickyListHeadersAdapter {

    public ItemListAdapter(List<FeedItem> list) {
        super(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        FeedItem feedItem = (FeedItem) mList.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_list_item, null);
            viewHolder.imageView = (NetworkImageView) convertView.findViewById(R.id.feed_item_list_item_img);
            viewHolder.imageView.setDefaultImageResId(R.drawable.ic_rss);
            viewHolder.imageView.setErrorImageResId(R.drawable.ic_rss);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.feed_item_list_item_title_txt);
            viewHolder.descriptionTextView = (TextView) convertView.findViewById(R.id.feed_item_list_item_description_txt);
            viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.feed_item_list_item_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageUrl(feedItem.getFeedSource().getFavicon(), VolleySingleton.getInstance().getImageLoader());
        viewHolder.titleTextView.setText(feedItem.getTitle());
        if (feedItem.getRead()) {
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
        FeedItem feedItem = (FeedItem) mList.get(position);
        if (convertView == null) {
            headerViewHolder = new HeaderViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_list_headers, null);
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
        long id = Long.valueOf(simpleDateFormat.format(((FeedItem) mList.get(position)).getDate()));
        return id;
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
