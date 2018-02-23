package com.wisdom;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    private String imageUrl;

    private TextView mHeading;
    private TextView mContent;
    private ImageView mImage;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);

        downloadUrl = getIntent().getStringExtra("downloadUrl");
        mHeading = (TextView) findViewById(R.id.text_article_heading);
        mContent = (TextView) findViewById(R.id.text_article_content);
        mImage = (ImageView) findViewById(R.id.img_article);
        mProgress = (ProgressBar) findViewById(R.id.prog_view_feed);

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
            mProgress.setVisibility(View.GONE);
            Glide.with(ViewNewsActivity.this)
                    .load(imageUrl)
                    .into(mImage);
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

                Element image = doc.select("div#quotablecontent > p > img").first();
                imageUrl = image.attr("src");

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
