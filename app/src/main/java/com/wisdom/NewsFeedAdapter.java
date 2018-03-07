package com.wisdom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by praji on 2/22/2018.
 */

public class NewsFeedAdapter extends ArrayAdapter<FeedItem> {

    private List<FeedItem> feedItemsList;

    public NewsFeedAdapter(Context context, int resource, List<FeedItem> objects) {
        super(context,resource,objects);

        feedItemsList = objects;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_newsfeed, parent, false);
        }

        TextView mArticleHeading = (TextView) view.findViewById(R.id.text_headline);
        TextView mActualContent = (TextView) view.findViewById(R.id.text_excerpt);
        ImageView mThumb = (ImageView) view.findViewById(R.id.img_thumb);
        final LinearLayout mShare = (LinearLayout) view.findViewById(R.id.btn_img_share);

        FeedItem feedItem = getItem(position);
        mArticleHeading.setText(feedItem.getTitle().getRendered());
        mActualContent.setText(feedItem.getExcerpt().getRendered());
        /*
        Glide.with(getContext())
                .load(feedItem.getImageUrl())
                .into(mThumb);
        */

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareContent;

                FeedItem clickedItem = getItem(position);
                shareContent = "Check out this article: \n" + clickedItem.getLink() + "\nShared from Wisdom Foundation";
                Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                getContext().startActivity(Intent.createChooser(sendIntent, "Share article with"));
            }
        });

        return view;
    }

    public FeedItem getItemClicked(int position){
        return feedItemsList.get(position);
    }
}
