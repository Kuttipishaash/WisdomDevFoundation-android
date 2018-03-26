package com.wisdom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shobhitpuri.custombuttons.GoogleSignInButton;
import com.wang.avi.AVLoadingIndicatorView;

public class LoginActivity extends AppCompatActivity {

    public static final int RC_GOOGLE_SIGN_IN = 1;
    public static final String TAG = "LoginActivity";
    private static final String PREF_FILE = "UserPreferences";

    private static long back_pressed;

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInButton mGoogleSignInButton;
    GoogleApiClient mGoogleApiClient;

    AVLoadingIndicatorView progressbar;

    Button mEmailSignInButton;
    TextView mEmailSignUpButton;
    Button mAnonymousSignInButton;

    View mButtonsPage;
    View mEmailPage;
    Button mSubmitEmailLogin;
    Button mSubmitEmailSignUp;
    EditText mEmailEditText;
    EditText mPasswordEditText;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseAuth.AuthStateListener mFirebaseAuthStateListner;

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        initViews();

        mFirebaseAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("/Users");
        mSharedPreferences = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        mEditor = mSharedPreferences.edit();

        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailSignInPage();
            }
        });

        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailSignUpPage();
            }
        });

        mAnonymousSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anonymousSignIn();
            }
        });
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        mFirebaseAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.d(TAG, "Logged in");

                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    //Getting user's profile image from google/email and adding it to shared preferences
                    Uri userPhotoURI = firebaseUser.getPhotoUrl();
                    if (userPhotoURI != null) {
                        mEditor.putString("user_image_url", userPhotoURI.toString());
                    } else {
                        mEditor.putString("user_image_url", "");
                    }
                    mEditor.putString("user_id", firebaseUser.getUid());
                    mEditor.putString("user_name", firebaseUser.getDisplayName());
                    mEditor.putString("user_email", firebaseUser.getEmail());
                    mEditor.apply();

                    Log.d("LOGIN", firebaseUser.getDisplayName());
                    UserInfo userInfo = new UserInfo(firebaseUser.getPhotoUrl().toString(), firebaseUser.getEmail(), firebaseUser.getUid(), firebaseUser.getDisplayName());
                    ref.child(userInfo.id_no).setValue(userInfo);


                    Toast.makeText(LoginActivity.this, "Signed in as : " + firebaseAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(LoginActivity.this, NewsFeedActivity.class));
                } else {
                    Log.d(TAG, "Logged out");
                }
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initViews() {
        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mAnonymousSignInButton = findViewById(R.id.anonymous_signin_button);
        mEmailSignUpButton = findViewById(R.id.tv_email_signup);
        mGoogleSignInButton = findViewById(R.id.google_signin_button);
        mButtonsPage = findViewById(R.id.buttons_layout);
        mEmailPage = findViewById(R.id.email_layout);
        mEmailEditText = findViewById(R.id.et_email);
        mPasswordEditText = findViewById(R.id.et_password);
        mSubmitEmailLogin = findViewById(R.id.btn_submit_email_signin);
        mSubmitEmailSignUp = findViewById(R.id.btn_submit_email_signup);
        progressbar=findViewById(R.id.pulse_bar);
    }


    private void showEmailSignUpPage() {
        mButtonsPage.setVisibility(View.GONE);
        mEmailPage.setVisibility(View.VISIBLE);
        mSubmitEmailLogin.setVisibility(View.GONE);
        mSubmitEmailSignUp.setVisibility(View.VISIBLE);
        emailSignUp();
    }

    private void showEmailSignInPage() {
        mButtonsPage.setVisibility(View.GONE);
        mEmailPage.setVisibility(View.VISIBLE);
        mSubmitEmailSignUp.setVisibility(View.GONE);
        mSubmitEmailLogin.setVisibility(View.VISIBLE);
        emailSignIn();
    }

    private void showButtonsPage() {
        mEmailPage.setVisibility(View.GONE);
        mButtonsPage.setVisibility(View.VISIBLE);
        mSubmitEmailSignUp.setVisibility(View.GONE);
        mSubmitEmailLogin.setVisibility(View.GONE);
    }

    private void emailSignUp() {
        mSubmitEmailSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(LoginActivity.this, "Logged in with email", Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Sign up failed. Sign in if already registered.",
                                            Toast.LENGTH_SHORT).show();
                                    showButtonsPage();
                                }
                            }
                        });
            }
        });
    }

    private void anonymousSignIn() {
        progressbar.setVisibility(View.VISIBLE);
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            Toast.makeText(LoginActivity.this, "Signed in as Anonymous", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Sign in failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void emailSignIn() {

        mSubmitEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Login failed. Sign up if you are not registered.",
                                            Toast.LENGTH_SHORT).show();
                                    showButtonsPage();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListner);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        progressbar.setVisibility(View.VISIBLE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onBackPressed(){
        if (back_pressed + 2000 > System.currentTimeMillis())super.onBackPressed();
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
