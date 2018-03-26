package com.wisdom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000; //3 seconds timeout for splash screen
    private static final int REQUEST_CODE_INTRO = 1;
    private static final String PREF_FILE = "UserPreferences";

    private CoordinatorLayout mCoordinatorLayout;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //To hide status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mSharedPreferences = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        mEditor = mSharedPreferences.edit();
        firebaseUser = null;
        boolean previouslyStarted = mSharedPreferences.getBoolean("first_start", false);
        if (!previouslyStarted) {
            mEditor.putBoolean("first_start", true);
            mEditor.apply();
            startActivityForResult(new Intent(SplashActivity.this, IntroActivity.class), REQUEST_CODE_INTRO);
        } else {
            //Shows splash screen for 3 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                    finish();
                }
            }, SPLASH_TIMEOUT);
        }
    }


    private void updateUI() {
        firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            finish();
            startActivity(new Intent(SplashActivity.this, NewsFeedActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            updateUI();
        }

    }

}
