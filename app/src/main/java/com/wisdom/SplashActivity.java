package com.wisdom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 5;
    private static final int SPLASH_TIMEOUT = 3000;
    private static final int REQUEST_CODE_INTRO = 1;
    private static final String PREF_FILE = "UserPreferences";
    //TODO:to remove
    int count = 0;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListner;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //To hide status bar
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        mSharedPreferences = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        mEditor = mSharedPreferences.edit();
        firebaseUser = null;
        boolean previouslyStarted = mSharedPreferences.getBoolean("first_start", false);
        if (!previouslyStarted) {
            mEditor.putBoolean("first_start", true);
            mEditor.apply();
            startActivityForResult(new Intent(SplashActivity.this, IntroActivity.class), REQUEST_CODE_INTRO);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    login();
                }
            }, SPLASH_TIMEOUT);
        }


    }

    private void login() {

        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                String userPhoto;
                count++;
                Log.d("ENTERED ELSE", "NOW ELSE IS ENTERED " + count);
                if (firebaseUser != null) {
                    try {
                        userPhoto = firebaseUser.getPhotoUrl().toString();
                        Log.d("Photo URL : ", userPhoto);
                    } catch (NullPointerException e) {
                        Log.e("Redirect Activity", "User photo URL is null");
                    }
                    String userID = firebaseUser.getUid();
                    String userEmail = firebaseUser.getEmail();
                    Toast.makeText(SplashActivity.this, "Logged in as : " + userEmail, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashActivity.this, NewsFeedActivity.class));
                    finish();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setTheme(R.style.FirebaseLoginTheme)
                                    .setLogo(R.drawable.wisdom_logo)
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListner);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            login();
            finish();
        }
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                this.finish();
                startActivity(new Intent(SplashActivity.this, NewsFeedActivity.class));
            } else {
                SplashActivity.this.finish();
            }

        }
    }

}
