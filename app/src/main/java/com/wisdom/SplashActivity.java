package com.wisdom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000;
    private static final int REQUEST_CODE_INTRO = 1;
    private static final String PREF_FILE = "UserPreferences";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSharedPreferences = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        mEditor = mSharedPreferences.edit();
        boolean previouslyStarted = mSharedPreferences.getBoolean("first_start", false);
        if (!previouslyStarted) {
            mEditor.putBoolean("first_start", true);
            mEditor.apply();
            startActivityForResult(new Intent(SplashActivity.this, IntroActivity.class), REQUEST_CODE_INTRO);

        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            /*
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, SPLASH_TIMEOUT); */
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));

            finish();
        }
    }
}
