package me.zsr.feeder.other;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.zsr.feeder.R;
import me.zsr.feeder.base.BaseActivity;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.DataModel;
import me.zsr.feeder.data.FeedReadException;
import me.zsr.feeder.data.FeedReader;
import me.zsr.feeder.data.FeedlyResult;
import me.zsr.feeder.data.IDataModel;
import me.zsr.feeder.source.SourceActivity;
import me.zsr.feeder.util.VolleySingleton;

public class AddSourceActivity extends BaseActivity {
    private SearchView mSearchView;
    private ListView mResultListView;
    private View mAddSourcePanel;
    private View mLoadingView;
    private View mRootView;

    private ArrayAdapter<String> mResultAdapter;
    private List<FeedlyResult> mResultList;
    private FeedReader mFeedReader;
    private IDataModel mDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_source);

        initEnvironment();
        initView();
        setListener();
    }

    private void initEnvironment() {
        mFeedReader = new FeedReader();
        mDataModel = new DataModel();
    }

    private void initView() {
        mRootView = findViewById(R.id.root_view);
        mAddSourcePanel = findViewById(R.id.add_source_panel);
        mLoadingView = findViewById(R.id.loading_view);
        mSearchView = (SearchView) findViewById(R.id.search_view);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
        // Set the query hint.
        mSearchView.setQueryHint("Hint");

        mResultListView = (ListView) findViewById(R.id.result_lv);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
    }

    private void setListener() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        searchFor(s);
                    } catch (URISyntaxException | MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else {
                    mResultListView.setVisibility(View.GONE);
                }
                return true;
            }
        });
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String url = mResultList.get(position).feedId.substring(5);

                new AsyncTask<Void, Void, FeedSource>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        showLoading();
                    }

                    @Override
                    protected FeedSource doInBackground(Void... params) {
                        try {
                            FeedSource feedSource = mFeedReader.load(url);
                            mDataModel.saveSource(feedSource);
                            mDataModel.saveItem(feedSource.getFeedItems(), feedSource.getId());
                            return feedSource;
                        } catch (FeedReadException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(FeedSource source) {
                        super.onPostExecute(source);

                        if (source != null) {
                            Intent intent = new Intent();
                            intent.putExtra("sourceId", source.getId());
                            setResult(SourceActivity.CODE_RESULT_SUCCESS, intent);
                            dismiss();
                        } else {
                            hideLoading();
                        }
                    }
                }.execute();
            }
        });
    }

    private void searchFor(final String input) throws URISyntaxException, MalformedURLException {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("query", input));
        final String requestUrl = URIUtils.createURI("http", "cloud.feedly.com", -1,
                "/v3/search/feeds", URLEncodedUtils.format(params, "utf-8"), null).toString();
        VolleySingleton.getInstance().getRequestQueue().cancelAll(this);
        StringRequest request = new StringRequest(requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!input.equals(mSearchView.getQuery().toString())) {
                            return;
                        }

                        try {
                            List<String> titleList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            String json = jsonObject.getString("results");
                            Type listType = new TypeToken<List<FeedlyResult>>() {}.getType();
                            mResultList = new Gson().fromJson(json, listType);

                            for (FeedlyResult result : mResultList) {
                                titleList.add(result.title);
                            }

                            if (titleList.size() > 0) {
                                mResultListView.setVisibility(View.VISIBLE);
                                mResultAdapter = new ArrayAdapter<>(AddSourceActivity.this,
                                        R.layout.result_list_item, R.id.result_txt,
                                        titleList.toArray(new String[titleList.size()]));
                                mResultListView.setAdapter(mResultAdapter);
                            } else {
                                mResultListView.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        request.setTag(this);
        VolleySingleton.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    public void dismiss() {
        ActivityCompat.finishAfterTransition(this);
        overridePendingTransition(0, 0);
    }

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mAddSourcePanel.setVisibility(View.GONE);
        mRootView.setClickable(false);
    }

    private void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
        mAddSourcePanel.setVisibility(View.VISIBLE);
        mRootView.setClickable(true);
    }
}
