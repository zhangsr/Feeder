package me.zsr.feeder.source;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.R;
import me.zsr.feeder.base.BaseListAdapter;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedDB;
import me.zsr.feeder.util.VolleySingleton;

/**
 * @description:
 * @author: Match
 * @date: 11/12/15
 */
public class SourceListAdapter extends BaseListAdapter {

    public SourceListAdapter(List<FeedSource> list) {
        super(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedSource feedSource = (FeedSource) mList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.source_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (NetworkImageView) convertView.findViewById(R.id.source_favicon_img);
            viewHolder.imageView.setErrorImageResId(R.drawable.ic_rss);
            viewHolder.imageView.setDefaultImageResId(R.drawable.ic_rss);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.source_title_txt);
            viewHolder.numTextView = (TextView) convertView.findViewById(R.id.source_item_num_txt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageUrl(feedSource.getFavicon(), VolleySingleton.getInstance().getImageLoader());
        viewHolder.titleTextView.setText(feedSource.getTitle());
        switch (App.getInstance().mCurrentMode) {
            case STAR:
                viewHolder.numTextView.setText("" + FeedDB.getInstance().countItemByStar(
                        feedSource.getId(), true));
                break;
            case UNREAD:
                viewHolder.numTextView.setText("" + FeedDB.getInstance().countItemByRead(
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
