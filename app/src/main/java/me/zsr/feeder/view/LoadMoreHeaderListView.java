package me.zsr.feeder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.zsr.feeder.R;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * @description:
 * @author: Match
 * @date: 9/6/15
 */
public class LoadMoreHeaderListView extends StickyListHeadersListView implements AbsListView.OnScrollListener {
    private LayoutInflater mInflater;
    private RelativeLayout mFooterView;
    private ProgressBar mLoadProgressBar;
    private TextView mLoadTextView;

    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mIsLoadingMore;
    private int mCurrentScrollState;

    public LoadMoreHeaderListView(Context context) {
        this(context, null);
    }

    public LoadMoreHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mFooterView = (RelativeLayout) mInflater.inflate(R.layout.load_more_footer, this, false);
        mFooterView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mLoadTextView = (TextView) mFooterView.findViewById(R.id.load_txt);
        mLoadProgressBar = (ProgressBar) mFooterView.findViewById(R.id.load_progress);

        addFooterView(mFooterView);

        super.setOnScrollListener(this);
    }

    @Override
    public void setAdapter(StickyListHeadersAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnLoadMoreListener != null) {
            if (visibleItemCount == totalItemCount) {
                mLoadProgressBar.setVisibility(View.GONE);
                mLoadTextView.setVisibility(View.GONE);
                return;
            }

            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

            if (!mIsLoadingMore && loadMore && mCurrentScrollState != SCROLL_STATE_IDLE) {
                mLoadProgressBar.setVisibility(View.VISIBLE);
                mLoadTextView.setVisibility(View.GONE);
                mIsLoadingMore = true;
                mOnLoadMoreListener.onLoadMore();
            }
        }

    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public void completeLoadMore() {
        mIsLoadingMore = false;
        mLoadProgressBar.setVisibility(View.GONE);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
