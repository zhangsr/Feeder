package me.zsr.feeder.base;

import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

import me.zsr.feeder.App;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/12/15
 */
public abstract class BaseListAdapter extends BaseAdapter {
    protected List<?> mList;
    protected LayoutInflater mLayoutInflater;

    public BaseListAdapter(List<?> list) {
        mList = list;
        mLayoutInflater = LayoutInflater.from(App.getInstance());
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged(List<?> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Deprecated
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        // Mark : Use notifyDataSetChanged(List<?>) instead
    }
}
