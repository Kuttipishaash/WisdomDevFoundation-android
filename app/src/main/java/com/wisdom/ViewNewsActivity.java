package com.wisdom;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;

public class ViewNewsActivity extends AppCompatActivity {

    private String downloadUrl;
    private String aricleTitle;
    private String articleContent;
    private TextView mHeading;
    private TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);

        downloadUrl = getIntent().getStringExtra("downloadUrl");
        mHeading = (TextView) findViewById(R.id.text_article_heading);
        mContent = (TextView) findViewById(R.id.text_article_content);

        new JsoupExtractTask().execute();
    }

    private class JsoupExtractTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mHeading.setText(aricleTitle);
            mContent.setText(articleContent);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(downloadUrl).get();

                Elements titles = doc.getElementsByClass("row title");
                for(Element title : titles) {
                    aricleTitle = title.text();
                }

                Elements contents = doc.getElementsByClass("entry-content");
                for(Element content : contents) {
                    articleContent = content.text();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
