package com.wisdom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 5;
    private static final int SPLASH_TIMEOUT = 3000; //3 seconds timeout for splash screen
    private static final int REQUEST_CODE_INTRO = 1;
    private static final String PREF_FILE = "UserPreferences";

    private CoordinatorLayout mCoordinatorLayout;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListner;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        //To hide status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("/Users");


        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_splash_coordinator_layout);

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
                String userPhotoURL;

                if (firebaseUser != null) {
                    try {
                        //Getting user's profile image from google/email and adding it to shared preferences
                        userPhotoURL = firebaseUser.getPhotoUrl().toString();
                        mEditor.putString("user_image_url", userPhotoURL);
                        mEditor.apply();
                    } catch (NullPointerException e) {
                        Log.e("Redirect Activity", "User photo URL is null");
                    }

                    Log.d("Username", firebaseUser.getDisplayName());
                    mEditor.putString("user_id", firebaseUser.getUid());
                    mEditor.putString("user_email", firebaseUser.getEmail());
                    mEditor.apply();

                    Log.d("LOGIN", firebaseUser.getDisplayName());
                    UserInfo userInfo = new UserInfo(firebaseUser.getPhotoUrl().toString(), firebaseUser.getEmail(), firebaseUser.getUid(), firebaseUser.getDisplayName());
                    ref.child(userInfo.id_no).setValue(userInfo);

                    Toast.makeText(SplashActivity.this, "Logged in as : " + userInfo.getId_no(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashActivity.this, NewsFeedActivity.class));
                    finish();
                } else {
                    // Sign in intent to FirebaseAuthUI
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
            } else if (response != null) {
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {

                    // Implementing snackbar with one action button
                    Snackbar snackbar = Snackbar
                            .make(mCoordinatorLayout, "No Network", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    login();
                                }
                            });

                    // Changing background color of snackbar to white
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.md_white_1000));

                    // Changing action button text color
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));

                    // Changing snackbar message text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.DKGRAY);
                    snackbar.show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(this, "Login was cancelled by user", Toast.LENGTH_SHORT).show();
                finish();
            }


        }
    }

}
