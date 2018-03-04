package com.wisdom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ViewNewsActivity extends AppCompatActivity {

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

    }

}
