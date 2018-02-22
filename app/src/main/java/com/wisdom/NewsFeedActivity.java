package com.wisdom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        disableSecurity();
        mFeedList = (ListView) findViewById(R.id.list_feed);
        feedItems = new ArrayList<FeedItem>();

        new JSoupLoaderTask().execute();
    }

    private class JSoupLoaderTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(NewsFeedActivity.this, "Load start", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mFeedAdapter = new NewsFeedAdapter(NewsFeedActivity.this, R.layout.item_newsfeed, feedItems);
            Toast.makeText(NewsFeedActivity.this, "Load end", Toast.LENGTH_SHORT).show();
            mFeedList.setAdapter(mFeedAdapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                List<String> headings = new ArrayList<String>();
                List<String> actualUrls = new ArrayList<String>();
                List<String> imageUrls = new ArrayList<String>();

                FeedItem feedItem = new FeedItem();
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

                for(int i = 0; i < 7; i++) {
                    feedItem = new FeedItem();
                    feedItem.setArticleHeading(headings.get(i));
                    feedItem.setActualUrl(actualUrls.get(i));
                    feedItem.setImageUrl(imageUrls.get(i));
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
