package com.example.android.newsapplication;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hind on 26/01/18.
 */

public final class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getName();

    private QueryUtils() {
    }

    public static List<News> fetchData(String requestUrl) throws JSONException {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<News> News = extractFeatureFromJson(jsonResponse);
        return News;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {   // If the request was successful (response code 200),// then read the input stream and parse the response.
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<News> extractFeatureFromJson(String newsJSON) throws JSONException {

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<News> newsItems = new ArrayList<>();
        try {
            JSONObject baseJSONObject = new JSONObject(newsJSON);
            JSONObject response = baseJSONObject.getJSONObject("response");
            JSONArray newsResults = response.getJSONArray("results");

            for (int i = 0; i < newsResults.length(); i++) {
                JSONObject news = newsResults.getJSONObject(i);
                String webTitle = news.getString("webTitle");
                String Date = news.getString("webPublicationDate");
                String Section = news.getString("sectionName");
                String WebURL = news.getString("webUrl");
                JSONArray tagsArray = news.getJSONArray("tags"); // Extract the value for the key called "byline" (author)
                String Author = "";
                if (news.has("tags")) {
                    if (tagsArray.length() > 0) {
                        for (int author = 0; author < 1; author++) {
                            JSONObject tags = tagsArray.getJSONObject(author);
                            if (tags.has("webTitle")) {
                                Author = tags.getString("webTitle");
                            }
                        }
                    }
                }

                newsItems.add(new News(webTitle, Date, Author, Section, WebURL));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        return newsItems;
    }
}