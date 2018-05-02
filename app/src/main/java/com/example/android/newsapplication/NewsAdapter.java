package com.example.android.newsapplication;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hind on 23/01/18.
 */


public class NewsAdapter extends ArrayAdapter<News> {

    public static final String LOG_TAG = NewsAdapter.class.getName();

    public NewsAdapter(Activity context, ArrayList<News> items) {
        super(context, 0, items);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        if (position < getCount()) {
            News currentItem = getItem(position);
            //this displays the title of news in the list
            TextView tw_title = (TextView) listItemView.findViewById(R.id.news_title);
            tw_title.setText(currentItem.getWebTitle());
            //this displays the authors name
            TextView tw_authors = (TextView) listItemView.findViewById(R.id.author);
            tw_authors.setText(currentItem.getAuthor());
            //this displays the date of publication
            TextView tw_date = (TextView) listItemView.findViewById(R.id.news_date);
            tw_date.setText(formatDate(currentItem.getDate()));
            //this displays the section
            TextView tw_section = (TextView) listItemView.findViewById((R.id.news_section));
            tw_section.setText(currentItem.getSection());
        }

        return listItemView;
    }

    public String formatDate(String date) {
        String newFormatData = "";
        if (date.length() >= 10) {
            CharSequence splittedDate = date.subSequence(0, 10);
            try {
                Date formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(splittedDate.toString());
                newFormatData = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(formatDate);
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            newFormatData = date;
        }
        return newFormatData;
    }
}