package com.example.android.newsapplication;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONException;

import java.util.List;

/**
 * Created by hind on 23/01/18.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private static final String LOG_TAG = NewsLoader.class.getName();

    private String mURL;

    public NewsLoader(Context context, String url) {
        super(context);
        mURL = url;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mURL == null) {
            return null;
        }
        List<News> news = null;
        try {
            news = QueryUtils.fetchData(mURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return news;
    }
}
