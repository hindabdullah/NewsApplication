package com.example.android.newsapplication;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int GUARDIAN_LOADER_ID = 1;
    private static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search?page-size=20&show-tags=contributor&show-fields=all&api-key=test";
    private TextView emptyStateTextView;
    private String query = null;
    private NewsAdapter Adapter;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ListView newsListview = (ListView) findViewById(R.id.list);
        Adapter = new NewsAdapter(this, new ArrayList<News>());

        emptyStateTextView = (TextView) findViewById(R.id.message_text_View);
        newsListview.setEmptyView(emptyStateTextView);
        newsListview.setAdapter(Adapter);
        newsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = Adapter.getItem(position);
                Uri newsUri = Uri.parse(currentNews.getWebURL());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

        if (isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(GUARDIAN_LOADER_ID, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet);
            emptyStateTextView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String maxResults = sharedPrefs.getString(
                getString(R.string.settings_max_results_key),
                getString(R.string.settings_max_results_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        String section = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default)
        );
        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (query != null && !query.isEmpty()) {
        }
        uriBuilder.appendQueryParameter("page-size", maxResults);
        uriBuilder.appendQueryParameter("order-by", orderBy.toLowerCase());
        if (!section.equals("all")) {
            uriBuilder.appendQueryParameter("section", section.toLowerCase());
        }
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);
        emptyStateTextView.setText(R.string.no_found);
        Adapter.clear();
        if (data != null && !data.isEmpty()) {
            Adapter.addAll(data);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
