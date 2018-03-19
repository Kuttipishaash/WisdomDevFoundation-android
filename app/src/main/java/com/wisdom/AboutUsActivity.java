package com.wisdom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
