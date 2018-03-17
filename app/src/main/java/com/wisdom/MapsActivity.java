package com.wisdom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by User on 22-Feb-18.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public LocationManager mLocationManager = null;
    SupportMapFragment  mapFragment;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    String cur_typ;
    private static final String PREF_FILE = "UserPreferences";
    String dp_url="";
    Marker mMarker;
    FloatingActionButton home_gps;
    ArrayList<Integer> lwr_activities=new ArrayList<Integer>();
    ArrayList<Integer> upr_activities=new ArrayList<Integer>();
    Marker my_marker=null;
    LatLng cur_location=null;
    LatLng destination=null;
    ArrayList<Institution> toilets=new ArrayList<Institution>();
    ArrayList<Institution> garbage=new ArrayList<Institution>();
    ArrayList<Institution> healthcare=new ArrayList<Institution>();
    ArrayList<Locations> locations=new ArrayList<Locations>();
    BottomSheetBehavior<View> mBottomSheetBehavior1;
    boolean bs_up;
    View bottomSheet;
    CircleImageView u_dp=null;
     Institution one,two,three;
    CommentAdapter commentLister = null;
    GoogleMap mMap=null,statmMap;
    int current_i;
    ProgressDialog progressBar;
    SmoothProgressBar loading = null;
    boolean flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = false;
        SharedPreferences dpuri=getSharedPreferences(PREF_FILE,MODE_PRIVATE);
        dp_url=dpuri.getString("user_image_url","");
        setContentView(R.layout.activity_maps);
        locationListenSet();
        registerInternetCheckReceiver();
        bottomSheetSetup();
        loading=(SmoothProgressBar) findViewById(R.id.loading1);
        home_gps = (FloatingActionButton) findViewById(R.id.gps_home);
        final SharedPreferences locpref = getSharedPreferences("UserDetails", MODE_PRIVATE);
        progressBar = new ProgressDialog(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Retrieving location");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        progressBar.setCancelable(false);

        cur_location = null;
    }
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     *  Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    boolean connctn=false;
    Snackbar snackbar=null;
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("ResourceType")
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                try {
                    if(connctn==false) {
                        if(snackbar!=null)
                            snackbar.dismiss();
                        snackbar = Snackbar
                                .make(findViewById(R.id.coordinatorlayout), "Connected", Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.RED);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(MapsActivity.this,Color.GREEN));
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();

                    }
                    connctn=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Network is changed or reconnected", Toast.LENGTH_LONG).show();
                try {
                    connctn=false;

                    snackbar = Snackbar
                            .make(findViewById(R.id.coordinatorlayout), "Cant connect to Wisdom network", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(Color.RED);

                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MapsActivity.this,Color.RED));
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Toast.makeText(this,"map ready",LENGTH_LONG).show();
        mMap = googleMap;
        statmMap = mMap;
        //setMyMarker(cur_location);
      //  drawRoute();
        home_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bs_up==false)
                {
                    if (cur_location != null)
                    {
                        CameraUpdate location=CameraUpdateFactory.newLatLngZoom(cur_location,15);
                        mMap.animateCamera(location);
                    }
                }
                else
                {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+cur_location.latitude
                                      +","+cur_location.longitude+"&daddr="+locations.get(current_i).loc.latitude+","+locations.get(current_i).loc.longitude+""));
                    startActivity(intent);                }
            }
        });
    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if(marker.getSnippet().equals("HAHA"))
                {
                    Toast.makeText(MapsActivity.this,"You are here!",LENGTH_LONG).show();
                }
                else {
                    bs_up=true;
                    current_i = Integer.parseInt(marker.getSnippet());
                    //home_gps.setImageResource(R.drawable.toilet);
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Button moredetails = (Button) findViewById(R.id.moredetails);
                    home_gps.setImageResource(R.drawable.navigation);
                    moredetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MapsActivity.this, LocationDetailer.class);
                            intent.putExtra("loc num", locations.get(Integer.parseInt(marker.getSnippet())).num);
                            intent.putExtra("loc type", marker.getTitle());
                            intent.putExtra("loc tot_comments", locations.get(Integer.parseInt(marker.getSnippet())).tot_comments);
                            intent.putExtra("loc name", locations.get(Integer.parseInt(marker.getSnippet())).name);
                            intent.putExtra("loc rating", locations.get(Integer.parseInt(marker.getSnippet())).rating);
                            intent.putExtra("loc address", locations.get(Integer.parseInt(marker.getSnippet())).address);
                            startActivity(intent);
                        }
                    });
                    TextView addrss_tv = (TextView) findViewById(R.id.bs_address_name);
                    TextView type_tv = (TextView) findViewById(R.id.bs_type);
                    TextView name_tv = (TextView) findViewById(R.id.bs_name);
                    ImageView rating_iv = (ImageView) findViewById(R.id.bs_rating);
                    addrss_tv.setText(locations.get(Integer.parseInt(marker.getSnippet())).address);
                    type_tv.setText(locations.get(Integer.parseInt(marker.getSnippet())).type);
                    name_tv.setText(locations.get(Integer.parseInt(marker.getSnippet())).name);
                    String rate_name = "";
                    switch (locations.get(Integer.parseInt(marker.getSnippet())).rating) {
                        case 1:
                            rate_name = "terrible";
                            break;

                        case 2:
                            rate_name = "bad";
                            break;

                        case 3:
                            rate_name = "okay";
                            break;

                        case 4:
                            rate_name = "good";
                            break;

                        case 5:
                            rate_name = "great";
                            break;
                    }
                    String uri = "@drawable/";  // where myresource (without the extension) is the file

                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    ImageView imageview = (ImageView) findViewById(R.id.bs_rating);
                    Drawable drawable = getResources().getDrawable(getResources().getIdentifier(rate_name, "drawable", getPackageName()));
                    imageview.setImageDrawable(drawable);
                }
                return true;
            }
        });
    }


    void retrieveTheNearServices()
    {
        loading.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        /////Toilet data
        myRef.child("Locations").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int i=0;
                if(dataSnapshot.getChildrenCount()!=0)
                for(DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        ++i;
                        if(30>getDistanceFromLatLonInKm(cur_location.latitude,cur_location.longitude,
                                Double.parseDouble(ds.child("lat").getValue().toString()),
                                Double.parseDouble(ds.child("lng" +
                                        "").getValue().toString())))
                        {
                            final Locations loctn=new Locations();
                            loctn.loc=new LatLng(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()));
                            loctn.num=Integer.parseInt(ds.child("id_no").getValue().toString());
                            loctn.type=ds.child("type").getValue().toString();
                            loctn.address=ds.child("address").getValue().toString();
                            loctn.name=ds.child("name").getValue().toString();
                            loctn.rating=Integer.parseInt(ds.child("rating").getValue().toString());
                            locations.add(loctn);
                            myRef.child("Comments").child(loctn.num+"").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot==null)
                                        loctn.tot_comments=0;
                                    else
                                        loctn.tot_comments=(int) dataSnapshot.getChildrenCount();
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });
                        }
                    }
                    if(locations.size()==0) {
                        loading.setVisibility(View.INVISIBLE);
                        snackbar = Snackbar
                                .make(findViewById(R.id.coordinatorlayout), "No service found nearby", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setActionTextColor(Color.RED);

                        View sbView = snackbar.getView();

                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.RED);
                        textView.setGravity(Gravity.CENTER);

                        snackbar.show();
                    }
                    else
                        setInstiMarker();
            }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }
    double getDistanceFromLatLonInKm(double lat1,double lon1,double lat2,double lon2) {
        double  R = 6371; // Radius of the earth in km
        double  dLat = deg2rad(lat2-lat1);  // deg2rad below
        double  dLon = deg2rad(lon2-lon1);
        double  a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2);
        double  c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double  d = R * c; // Distance in km
        return d;
    }

    double  deg2rad(double deg)
    {
        return deg * (Math.PI/180);
    }

    void setInstiMarker()
    {
        int i=0;
        Log.d("location size= ",locations.size()+"");

        for(i=0;i<locations.size();++i)
        {
            Log.d("locations details  ",""+locations.get(i).loc);
            final View mrker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.insti_marker, null);
            final ImageView rdp = (ImageView) mrker.findViewById(R.id.insti_dp);
            File localFile = null;
            try {
                localFile = File.createTempFile("images", "png");
            } catch (IOException e) {
                e.printStackTrace();
            }

            final File finalLocalFile = localFile;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final int finalI = i;
            final int finalI1 = i;
            storageReference.child("display pictures/"+locations.get(i).type+".png").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                    rdp.setImageBitmap(bitmap);
                    LatLng ll = locations.get(finalI).loc;
                    MarkerOptions options = new MarkerOptions().title(locations.get(finalI).type).snippet(finalI +"").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
                    mMarker=mMap.addMarker(options);
                    if(finalI1 ==locations.size()-1)
                    {
                        loading.setVisibility(View.INVISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });


        }
        Log.d("firebase checking","Markers are the set");

    }

       void setMyMarker(final LatLng loc)
       {

           if(my_marker!=null)
               my_marker.remove();
           loading.setVisibility(View.VISIBLE);
           SharedPreferences locpref= getSharedPreferences("UserDetails", MODE_PRIVATE);
             new DownloadDp().execute();
           if(flag==false)
                changeCam(loc);

       }
    public Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    ////////////////////    location listener
    void locationListenSet()
    {
        initializeLocationManager();
        MapsActivity.LocationListener[] mLocationListeners = new MapsActivity.LocationListener[] {
                new MapsActivity.LocationListener(LocationManager.GPS_PROVIDER),
                new MapsActivity.LocationListener(LocationManager.NETWORK_PROVIDER)
        };
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    public class LocationListener implements android.location.LocationListener
    {
        public Location mLastLocation;
        int i=0;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);

        }

        @Override
        public void onLocationChanged(Location location)
        {
            cur_location=new LatLng(location.getLatitude(),location.getLongitude());
            if(mMap!=null)
            {
                setMyMarker(cur_location);
                if(flag==false) {
                    retrieveTheNearServices();
                    progressBar.dismiss();
                }
            }
            flag=true;
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }


    }

    public void changeCam(LatLng ll)
    {
        CameraUpdate location= CameraUpdateFactory.newLatLngZoom(ll,15);
        mMap.animateCamera(location);

    }
    private void bottomSheetSetup(){
        bottomSheet = findViewById(R.id.btm_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(0);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // mBottomSheetBehavior1.setPeekHeight(bottomSheet.getHeight());
                    mBottomSheetBehavior1.setPeekHeight(0);
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setPeekHeight(0);
                    bs_up=false;

                    home_gps.setImageResource(R.drawable.gps_home);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
    }


    private class DownloadDp extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... URL) {

            Bitmap bitmap = null;
            try {
                InputStream input = new java.net.URL(dp_url).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            View mrker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
            final CircleImageView rdp = (CircleImageView) mrker.findViewById(R.id.imageView1);
            u_dp=rdp;
            if(result!=null)
                u_dp.setImageBitmap(result);
            else
                u_dp.setImageResource(R.drawable.defaultdp);
            LatLng ll = new LatLng(cur_location.latitude,cur_location.longitude);
            MarkerOptions options = new MarkerOptions().title("ME").snippet("HAHA").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            my_marker=mMap.addMarker(options);
            loading.setVisibility(View.INVISIBLE);
        }
    }
}
