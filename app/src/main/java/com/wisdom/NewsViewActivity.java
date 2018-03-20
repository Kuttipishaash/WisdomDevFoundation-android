package com.wisdom;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsViewActivity extends AppCompatActivity {

    TextView mArticleContent;
    CollapsingToolbarLayout mToolbarLayout;

    String articleHeading;
    String articleLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        //Initialize views
        mToolbarLayout = findViewById(R.id.toolbar_layout_view_news);
        mArticleContent = findViewById(R.id.text_article_content);

        //Get heading, article link and content from the calling intent
        articleHeading = getIntent().getStringExtra("articleHeading");
        articleLink = getIntent().getStringExtra("articleLink");
        mArticleContent.setText(getIntent().getStringExtra("articleContent"));

        //Initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_news);
        toolbar.setTitle(articleHeading);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get retrofit instance of FeedClient
        FeedClient feedClient = Utils.getFeedClientRef();

        //Create Call object imageCall and initialize it with obtained feedClient by passing id of image that was obtained from calling intent
        Call<FeedImage> imageCall = feedClient.image(getIntent().getStringExtra("articleImageId"));

        //Start async call
        imageCall.enqueue(new Callback<FeedImage>() {

            //On successful response
            @Override
            public void onResponse(Call<FeedImage> call, Response<FeedImage> response) {
                Glide
                        .with(getApplicationContext())
                        .load(response.body().getGuid().getRendered())
                        .apply(new RequestOptions()
                                .centerCrop()
                        )
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                resource.setAlpha(200);
                                mToolbarLayout.setBackground(resource);
                            }
                        });
            }

            //On failed response
            @Override
            public void onFailure(Call<FeedImage> call, Throwable t) {
                //TODO: Include some UI change
            }
        });

        //Share button implementation
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view_news);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareContent;
                shareContent = "Check out this article: \n" + articleHeading + "\n" + articleLink + "\nShared from Wisdom Foundation";
                Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                startActivity(Intent.createChooser(shareIntent, "Share article with "));
            }
        });
    }
}
