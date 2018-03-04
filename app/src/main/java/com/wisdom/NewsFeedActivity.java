package com.wisdom;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by praji on 2/22/2018.
 */

public class NewsFeedActivity extends AppCompatActivity {

    private List<FeedItem> feedItems;
    private ListView mFeedList;
    private NewsFeedAdapter mFeedAdapter;

    private DrawerLayout mDrawerLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.news_toolbar);
        setSupportActionBar(toolbar);
*/
        setTitle("News");

        initNavDrawer();

        progressBar = (ProgressBar) findViewById(R.id.prog_newsfeed);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://wisdominitiatives.org")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder
                .client(httpClient.build())
                .build();
        FeedClient feedClient = retrofit.create(FeedClient.class);
        Call<List<FeedItem>> call = feedClient.feeds();
        call.enqueue(new Callback<List<FeedItem>>() {
            @Override
            public void onResponse(Call<List<FeedItem>> call, Response<List<FeedItem>> response) {

                List<FeedItem> feed = response.body();

                mFeedList = (ListView) findViewById(R.id.list_feed);
                mFeedList.setAdapter(new NewsFeedAdapter(NewsFeedActivity.this, R.layout.item_newsfeed, feed));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<FeedItem>> call, Throwable t) {
                Toast.makeText(NewsFeedActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });


    /*
        mFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FeedItem item = mFeedAdapter.getItemClicked(position);

                Intent intent = new Intent(NewsFeedActivity.this, ViewNewsActivity.class);
                intent.putExtra("downloadUrl", item.getActualUrl());
                startActivity(intent);
            }
        });
        new JSoupLoaderTask().execute();
        */
    }
/*
    private class JSoupLoaderTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.GONE);
            mFeedAdapter = new NewsFeedAdapter(NewsFeedActivity.this, R.layout.item_newsfeed, feedItems);
            mFeedList.setAdapter(mFeedAdapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                List<String> headings = new ArrayList<String>();
                List<String> actualUrls = new ArrayList<String>();
                List<String> imageUrls = new ArrayList<String>();
                List<String> sampleContents = new ArrayList<String>();

                FeedItem feedItem;

                Document doc = Jsoup.connect("https://blog.udacity.com").get();

                Elements articles = doc.select("article > header > h1 > a");
                for (Element article : articles) {
                    headings.add(article.text());
                    actualUrls.add(article.attr("href"));
                }

                Elements images = doc.select("article > div > p > img");
                for (Element image : images) {
                    imageUrls.add(image.attr("src"));
                }

                Elements contents = doc.getElementsByClass("entry-content");
                for (Element content : contents) {
                    sampleContents.add(content.text().substring(0, 130));
                }

                for (int i = 0; i < 7; i++) {
                    feedItem = new FeedItem();
                    feedItem.setArticleHeading(headings.get(i));
                    feedItem.setActualUrl(actualUrls.get(i));
                    feedItem.setImageUrl(imageUrls.get(i));
                    feedItem.setShortContent(sampleContents.get(i));
                    feedItems.add(feedItem);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
*/
    private void disableSecurity() {
        // Create a trust manager that does not validate certificate chains like the default
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Log.e("ERR_DISABLE_SECURITY", "Security has note been dsiabled");
        }

    }

    private void initNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.nav_contact:
                        startActivity(new Intent(NewsFeedActivity.this, ContactUsActivity.class));
                        break;
                    case R.id.nav_info:
                        startActivity(new Intent(NewsFeedActivity.this, AboutUsActivity.class));
                        break;
                    case R.id.nav_map:
                        startActivity(new Intent(NewsFeedActivity.this, MapsActivity.class));
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
    }
}
