package com.wisdom;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by praji on 2/22/2018.
 */

public class NewsFeedAdapter extends ArrayAdapter<FeedItem> {

    public NewsFeedAdapter(Context context, int resource, List<FeedItem> objects) {
        super(context,resource,objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_newsfeed, parent, false);
        }

        TextView mArticleHeading = (TextView) view.findViewById(R.id.text_headline);
        TextView mActualUrl = (TextView) view.findViewById(R.id.text_download_url);
        ImageView mThumb = (ImageView) view.findViewById(R.id.img_thumb);

        FeedItem feedItem = getItem(position);
        mArticleHeading.setText(feedItem.getArticleHeading());
     //   mActualUrl.setText(feedItem.getactualUrl());

        return view;
    }
}
