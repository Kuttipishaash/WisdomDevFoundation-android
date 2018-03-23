package com.wisdom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        setTitle("News");

        initNavDrawer();

        //Initialize views
        progressBar = findViewById(R.id.prog_newsfeed);
        mFeedList = findViewById(R.id.list_feed);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(NewsFeedActivity.this, MapsActivity.class);
//                startActivity(intent);
//            }
//        });
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.fab);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.fab_maps);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.fab_emg);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(NewsFeedActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                Context context = getApplicationContext();
                CharSequence text = "Hello toast!";
                Toast toast = Toast.makeText(context, "Upcoming Service", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        //Initialize instance of Feedclient with REST call
        FeedClient feedClient = Utils.getFeedClientRef();

        //Initialize Call object for async call of feeds object in FeedClient interface
        Call<List<FeedItem>> call = feedClient.feeds();

        //Start async call
        call.enqueue(new Callback<List<FeedItem>>() {

            //On successful response
            @Override
            public void onResponse(Call<List<FeedItem>> call, Response<List<FeedItem>> response) {

                //Store response to List of FeedItem
                List<FeedItem> feed = response.body();

                if(mFeedList != null && feed != null) {

                    //Initialize adapter and set it to list view
                    mFeedAdapter = new NewsFeedAdapter(NewsFeedActivity.this, R.layout.item_newsfeed, feed);
                    mFeedList.setAdapter(mFeedAdapter);

                    //Hide progress bar
                    progressBar.setVisibility(View.GONE);
                } else {
                    showNetworkFailure();
                }

                //On clicking any item in the list, run the following
                mFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //Get the item that was clicked
                        FeedItem item = mFeedAdapter.getItemClicked(position);

                        //Start NewsViewActivity and pass it extra data
                        Intent intent = new Intent(NewsFeedActivity.this, NewsViewActivity.class);
                        intent.putExtra("articleHeading", item.getTitle().getRendered()); //heading of article
                        intent.putExtra("articleContent", item.getContent().getRendered()); //content of article
                        intent.putExtra("articleLink", item.getLink()); //link of article
                        intent.putExtra("articleImageId", item.getFeaturedMedia()); //id of image in article
                        startActivity(intent);
                    }
                });
            }

            //On failed response
            @Override
            public void onFailure(Call<List<FeedItem>> call, Throwable t) {
                showNetworkFailure();
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

    private void showNetworkFailure() {
        //Initialize container for no network fragment
        FrameLayout frameLayout = findViewById(R.id.fragment_newsfeed_container);

        //Initialize fragment of NoNetworkFragment
        Fragment fragment = new NoNetworkFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    //    fragmentTransaction.disallowAddToBackStack();
    //    fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.add(R.id.fragment_newsfeed_container, fragment);

        //Hide the container view of existing news feed view
        View view = findViewById(R.id.activity_news_container);
        view.setVisibility(View.GONE);

        //Make fragment container visible
        frameLayout.setVisibility(View.VISIBLE);

        fragmentTransaction.commit();

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
