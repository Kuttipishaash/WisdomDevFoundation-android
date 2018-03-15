package com.wisdom;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsViewActivity extends AppCompatActivity {

    TextView mArticleContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_news);
        toolbar.setTitle(getIntent().getStringExtra("articleHeading"));
        setSupportActionBar(toolbar);

        mArticleContent = findViewById(R.id.text_article_content);
        mArticleContent.setText(getIntent().getStringExtra("articleContent"));
/*
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
                Glide.with(NewsViewActivity.this)
                        .load(response.body().getGuid().getRendered())
                        .into(mImage);
            }

            @Override
            public void onFailure(Call<FeedImage> call, Throwable t) {

            }
        });
*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view_news);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
