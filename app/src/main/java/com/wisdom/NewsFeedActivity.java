package com.wisdom;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

/**
 * Created by praji on 2/22/2018.
 */

public class NewsFeedActivity extends AppCompatActivity {

    private List<FeedItem> feedItems;
    private ListView mFeedList;
    private NewsFeedAdapter mFeedAdapter;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        setTitle("News");

        disableSecurity();
        mFeedList = (ListView) findViewById(R.id.list_feed);
        feedItems = new ArrayList<FeedItem>();

        progressBar = (ProgressBar) findViewById(R.id.prog_newsfeed);

        mFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FeedItem item = mFeedAdapter.getItemClicked(position);

                Intent intent = new Intent(NewsFeedActivity.this, ViewNewsActivity.class);
                intent.putExtra("downloadUrl", item.getActualUrl());
                startActivity(intent);
                /*
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(NewsFeedActivity.this, Uri.parse(item.getActualUrl()));
                */
            }
        });
        new JSoupLoaderTask().execute();
    }

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

}