package com.wisdom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFeedActivity extends AppCompatActivity {

    private List<FeedItem> feedItems;
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

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://wisdominitiatives.org")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder
                .client(httpClient.build())
                .build();
        FeedClient feedClient = retrofit.create(FeedClient.class);
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

                        Intent intent = new Intent(NewsFeedActivity.this, ViewNewsActivity.class);
                        intent.putExtra("articleHeading", item.getTitle().getRendered());
                        intent.putExtra("articleContent", item.getContent().getRendered());
                        intent.putExtra("articleImageId", item.getFeaturedMedia());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<FeedItem>> call, Throwable t) {
                //TODO: set network handler
                Toast.makeText(NewsFeedActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
}
