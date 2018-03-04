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

    }

}
