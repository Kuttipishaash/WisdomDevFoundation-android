package com.wisdom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFeedActivity extends AppCompatActivity {

    private static final String PREF_FILE = "UserPreferences";

    private ListView mFeedList;
    private NewsFeedAdapter mFeedAdapter;

    private DrawerLayout mDrawerLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        setTitle("News");

        initNavDrawer();

        progressBar = findViewById(R.id.prog_newsfeed);

        FeedClient feedClient = Utils.getFeedClientRef();
        Call<List<FeedItem>> call = feedClient.feeds();
        call.enqueue(new Callback<List<FeedItem>>() {
            @Override
            public void onResponse(Call<List<FeedItem>> call, Response<List<FeedItem>> response) {

                List<FeedItem> feed = response.body();

                mFeedList = findViewById(R.id.list_feed);
                mFeedAdapter = new NewsFeedAdapter(NewsFeedActivity.this, R.layout.item_newsfeed, feed);

                mFeedList.setAdapter(mFeedAdapter);
                progressBar.setVisibility(View.GONE);

                mFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FeedItem item = mFeedAdapter.getItemClicked(position);

                        Intent intent = new Intent(NewsFeedActivity.this, NewsViewActivity.class);
                        intent.putExtra("articleHeading", item.getTitle().getRendered());
                        intent.putExtra("articleContent", item.getContent().getRendered());
                        intent.putExtra("articleLink", item.getLink());
                        intent.putExtra("articleImageId", item.getFeaturedMedia());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<FeedItem>> call, Throwable t) {
                FrameLayout frameLayout = findViewById(R.id.fragment_newsfeed_container);

                Fragment fragment = new NoNetworkFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fragment_newsfeed_container, fragment);

                View view = findViewById(R.id.activity_news_container);
                view.setVisibility(View.GONE);

                frameLayout.setVisibility(View.VISIBLE);
                Toast.makeText(NewsFeedActivity.this, "Fail", Toast.LENGTH_SHORT).show();

                fragmentTransaction.commit();
            }
        });
    }

    private void initNavDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.nav_contact:
                        startActivity(new Intent(NewsFeedActivity.this, ContactUsActivity.class));
                        break;
                    case R.id.nav_info:
                        startActivity(new Intent(NewsFeedActivity.this, AboutUsActivity.class));
                        break;
                    case R.id.nav_map:
                        startActivity(new Intent(NewsFeedActivity.this, MapsActivity.class));
                        break;
                    case R.id.nav_share:
                        //TODO:Share app functionality
                        break;
                    case R.id.nav_logout:
                        logout();
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
    }

    //To open the nav drawer when the menu icon on the action bar is tapped
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //To close nav drawer on back button press in case the drawer is open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Function to logout using Firebase AuthUI
    private void logout() {
        SharedPreferences mSharedPreferences = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove("user_image_url");
        editor.remove("user_id");
        editor.remove("user_email");
        editor.apply();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        finish();
                        startActivity(new Intent(NewsFeedActivity.this, SplashActivity.class));
                    }
                });

    }
}
