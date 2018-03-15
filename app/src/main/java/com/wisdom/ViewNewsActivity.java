package com.wisdom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewNewsActivity extends AppCompatActivity {
/*
    private TextView mHeading;
    private TextView mContent;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);

        mHeading = (TextView) findViewById(R.id.text_article_heading);
        mContent = (TextView) findViewById(R.id.text_article_content);
        mImage = (ImageView) findViewById(R.id.img_article);

        mHeading.setText(getIntent().getStringExtra("articleHeading"));
        mContent.setText(getIntent().getStringExtra("articleContent"));


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
                Glide.with(ViewNewsActivity.this)
                        .load(response.body().getGuid().getRendered())
                        .into(mImage);
            }

            @Override
            public void onFailure(Call<FeedImage> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_news, menu);
        return true;
    }

    //and this to handle actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
}
