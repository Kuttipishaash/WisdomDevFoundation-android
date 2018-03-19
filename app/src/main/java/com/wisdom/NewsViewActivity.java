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

        articleHeading = getIntent().getStringExtra("articleHeading");
        articleLink = getIntent().getStringExtra("articleLink");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_news);
        toolbar.setTitle(articleHeading);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbarLayout = findViewById(R.id.toolbar_layout_view_news);

        mArticleContent = findViewById(R.id.text_article_content);
        mArticleContent.setText(getIntent().getStringExtra("articleContent"));

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://wisdominitiatives.org")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder
                .client(httpClient.build())
                .build();
        FeedClient feedClient = retrofit.create(FeedClient.class);
        Call<FeedImage> imageCall = feedClient.image(getIntent().getStringExtra("articleImageId"));

        imageCall.enqueue(new Callback<FeedImage>() {
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

            @Override
            public void onFailure(Call<FeedImage> call, Throwable t) {

            }
        });

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
