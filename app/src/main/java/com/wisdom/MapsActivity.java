package com.wisdom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smilerating.SmileRating;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.floatingmenu.FloatingActionItem;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by User on 22-Feb-18.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public LocationManager mLocationManager = null;
    SupportMapFragment  mapFragment;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    BottomSheetBehavior<View> mBottomSheetBehavior1;
    Location cur_location=null;
    ArrayList<Institution> toilets=new ArrayList<Institution>();
    ArrayList<Institution> garbage=new ArrayList<Institution>();
    ArrayList<Institution> healthcare=new ArrayList<Institution>();
    View bottomSheet;
    String[] person_name={"Kuttapan","Shaji","Bilal","Jose","Prabhakaran","Romy","Enthiran","Tonikutan"};
    String[] person_comment={"Havent seen such a beutiful place",
                             "Top notch all the way",
                            "Cant believe how come everbody here is so nice",
                            "Found a dead cockroach inside there...",
                            "One of immemorable experience in my life","Want to visit again",
                            "Loved this place...",
                            "Worth visiting",
                            "Great service.."};
    String[] dp_name={"a","b","c","d","e","f","g"};
    Marker my_marker=null;
    Institution one,two,three;
    GoogleMap mMap=null,statmMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationListenSet();
        final SharedPreferences locpref= getSharedPreferences("UserDetails", MODE_PRIVATE);
        final ProgressDialog progressBar = new ProgressDialog(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Retrieving location");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        new Thread(new Runnable() {
            public void run() {
                while (locpref.getString("lat","").equals("")||cur_location==null);
                progressBar.dismiss();
                retrieveTheNearServices();
                Log.d("firebase checking","yaa fine");
            }
        }).start();

    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Toast.makeText(this,"map ready",LENGTH_LONG).show();
        mMap = googleMap;
        statmMap = mMap;
        bottomSheet = (View)findViewById(R.id.btm_sheet);
        final BottomSheetBehavior mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
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
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                BottomSheetBehavior mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
                mBottomSheetBehavior1.setPeekHeight(450);
                View bottomSheet = findViewById(R.id.btm_sheet);
                TextView address=(TextView)bottomSheet.findViewById(R.id.address_name);
                address.setSelected(true);
                TextView type=(TextView)bottomSheet.findViewById(R.id.type);
                LinearLayout imgs=(LinearLayout)bottomSheet.findViewById(R.id.imgs);
                SmileRating sr=(SmileRating)bottomSheet.findViewById(R.id.smile_rating);
                String imageName = "";
                Button addcomnt=(Button)bottomSheet.findViewById(R.id.button2);
                ListView comments=(ListView)bottomSheet.findViewById(R.id.comments);
                CommentAdapter commentLister;
                addcomnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
                if(marker.getTitle().equals("Toilet"))
                {
                    imageName="toilet";
                    address.setText(toilets.get(Integer.parseInt(marker.getSnippet())).address);
                    type.setText(marker.getTitle());
                    sr=setRating(sr,toilets.get(Integer.parseInt(marker.getSnippet())).rating);
                    for(int i=0;i<4;++i)
                    {
                        Institution.CommentsRating one=new Institution.CommentsRating();
                        one.name=person_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        one.comment=person_comment[ThreadLocalRandom.current().nextInt(0, 7)];
                        one.dp=dp_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        toilets.get(Integer.parseInt(marker.getSnippet())).cmmnts.add(one);
                    }
                    commentLister=new CommentAdapter(MapsActivity.this,toilets.get(Integer.parseInt(marker.getSnippet())).cmmnts);
                    comments.setAdapter(commentLister);

                }
                else if(marker.getTitle().equals("Garbage"))
                {
                    imageName="garbage";
                    address.setText(garbage.get(Integer.parseInt(marker.getSnippet())).address);
                    type.setText(marker.getTitle());
                    sr=setRating(sr,garbage.get(Integer.parseInt(marker.getSnippet())).rating);
                    for(int i=0;i<4;++i)
                    {
                        Institution.CommentsRating one=new Institution.CommentsRating();
                        one.name=person_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        one.comment=person_comment[ThreadLocalRandom.current().nextInt(0, 7)];
                        one.dp=dp_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        garbage.get(Integer.parseInt(marker.getSnippet())).cmmnts.add(one);
                    }
                    commentLister=new CommentAdapter(MapsActivity.this,garbage.get(Integer.parseInt(marker.getSnippet())).cmmnts);
                    comments.setAdapter(commentLister);
                }
                else if(marker.getTitle().equals("Healthcare"))
                {
                    imageName="healthcare";
                    address.setText(healthcare.get(Integer.parseInt(marker.getSnippet())).address);
                    type.setText(marker.getTitle());
                    for(int i=0;i<4;++i)
                    {
                        Institution.CommentsRating one=new Institution.CommentsRating();
                        one.name=person_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        one.comment=person_comment[ThreadLocalRandom.current().nextInt(0, 7)];
                        one.dp=dp_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        healthcare.get(Integer.parseInt(marker.getSnippet())).cmmnts.add(one);
                    }
                    commentLister=new CommentAdapter(MapsActivity.this,healthcare.get(Integer.parseInt(marker.getSnippet())).cmmnts);
                    comments.setAdapter(commentLister);
                    sr=setRating(sr,healthcare.get(Integer.parseInt(marker.getSnippet())).rating);
                }

                for(int i=0;i<4;i++)
                {
                    LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View rView = inflater.inflate(R.layout.insti_btm_sheet_imgs, null);
                    int resID = getResources().getIdentifier(imageName+ ThreadLocalRandom.current().nextInt(1, 8 + 1)
,"drawable", "com.wisdom");
                    ImageView image=(ImageView)rView.findViewById(R.id.insti_img);
                    image.setImageResource(resID );
                    imgs.addView(rView);
                }
                return false;
            }
        });
    }
    SmileRating setRating(SmileRating sr,String rating)
    {
       if(rating.equals("TERRIBLE"))
           sr.setSelectedSmile(1);
       else if(rating.equals("BAD"))
        sr.setSelectedSmile(2);
       else if(rating.equals("OKAY"))
           sr.setSelectedSmile(3);
       else if(rating.equals("GOOD"))
           sr.setSelectedSmile(4);
        else
            sr.setSelectedSmile(5);
        return sr;
    }
    void retrieveTheNearServices()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        /////Toilet data
        myRef.child("Toilet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                toilets=new ArrayList<Institution>();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        one=new Institution();
                        one.loc=new LatLng(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()));
                        one.num=ds.child("id_no").getValue().toString();
                        one.type="Toilet";
                        one.rating=ds.child("rating").getValue().toString();

                        toilets.add(one);
                    }

                ////Trash data
                myRef.child("Garbage").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        garbage=new ArrayList<Institution>();
                        for(DataSnapshot ds:dataSnapshot.getChildren())
                        {
                            one=new Institution();
                            one.loc=new LatLng(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()));
                            one.num=ds.child("id_no").getValue().toString();
                            one.type="Garbage";
                            one.rating=ds.child("rating").getValue().toString();
                            garbage.add(one);
                        }
                        /////Health care data
                        myRef.child("Healthcare").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                healthcare=new ArrayList<Institution>();
                                for(DataSnapshot ds:dataSnapshot.getChildren())
                                {
                                    one=new Institution();
                                    one.loc=new LatLng(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()));
                                    one.num=ds.child("id_no").getValue().toString();
                                    one.type="Healthcare";
                                    one.rating=ds.child("rating").getValue().toString();
                                    healthcare.add(one);
                                }
                                setInstiMarker();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
    void setInstiMarker()
    {
        int i=0;
        for(i=0;i<toilets.size();++i)
        {
            View mrker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.insti_marker, null);
            ImageView rdp = (ImageView) mrker.findViewById(R.id.insti_dp);
            toilets.get(i).address=getAddress(toilets.get(i).loc);
            rdp.setImageResource(R.drawable.toilet);
            LatLng ll = toilets.get(i).loc;
            MarkerOptions options = new MarkerOptions().title("Toilet").snippet(i+"").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            mMap.addMarker(options);

            ////////////
            rdp.setImageResource(R.drawable.trash);
            ll = garbage.get(i).loc;
            garbage.get(i).address=getAddress(garbage.get(i).loc);
            options = new MarkerOptions().title("Garbage").snippet(i+"").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            mMap.addMarker(options);

            /////////////
            rdp.setImageResource(R.drawable.healthcare);
            ll = healthcare.get(i).loc;
            healthcare.get(i).address=getAddress(healthcare.get(i).loc);
            options = new MarkerOptions().title("Healthcare").snippet(i+"").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            mMap.addMarker(options);
        }
        Log.d("firebase checking","Markers are the set");

    }

       void setMyMarker(final Location loc)
       {
            SharedPreferences locpref= getSharedPreferences("UserDetails", MODE_PRIVATE);
            View mrker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
            final CircleImageView rdp = (CircleImageView) mrker.findViewById(R.id.imageView1);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultdp);
            rdp.setImageBitmap(icon);
            LatLng ll = new LatLng(loc.getLatitude(),loc.getLongitude());
            MarkerOptions options = new MarkerOptions().title("ME").snippet("HAHA").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            if (my_marker == null)
            {
                my_marker = mMap.addMarker(options);
            }
            else
            {
                my_marker.remove();
                my_marker = mMap.addMarker(options);
            }
            SharedPreferences.Editor editloc=locpref.edit();
            editloc.putString("lat",loc.getLatitude()+"");
            editloc.putString("lng",loc.getLongitude()+"");
            editloc.commit();
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
  String getAddress(LatLng loc)
    {

        return "kishkindapuri p.o. kothamangalam";
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
    public void changeCam(LatLng ll)
    {
        CameraUpdate location= CameraUpdateFactory.newLatLngZoom(ll,15);
        mMap.animateCamera(location);

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
           cur_location=location;
           Toast.makeText(MapsActivity.this,"Location changed "+cur_location,LENGTH_LONG).show();
           setMyMarker(location);
           changeCam(new LatLng(location.getLatitude(),location.getLongitude()));
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

}
