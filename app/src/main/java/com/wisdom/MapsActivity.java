package com.wisdom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.floatingmenu.FloatingActionItem;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by User on 22-Feb-18.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    Location cur_location=null;
    ArrayList<Institution> toilets=new ArrayList<Institution>();
    ArrayList<Institution> garbage=new ArrayList<Institution>();
    ArrayList<Institution> healthcare=new ArrayList<Institution>();

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
        progressBar.setCancelable(true);
        progressBar.setMessage("Retrieving location");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        new Thread(new Runnable() {
            public void run() {
                while (locpref.getString("lat","").equals("")&&cur_location==null);
                progressBar.dismiss();
                retrieveTheNearServices();
                Log.d("firebase checking","yaa fine");


            }
        }).start();

    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        statmMap = mMap;
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTitle().equals("Toilet"))
                {

                }
                else if(marker.getTitle().equals("Garbage"))
                {

                }
                else if(marker.getTitle().equals("Trash"))
                {

                }
                return false;
            }
        });
    }
    void retrieveTheNearServices()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        /////Toilet data
        myRef.child("Toilet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                one=two=three=new Institution();
                one.loc=new LatLng(Double.parseDouble(dataSnapshot.child("1").child("lat").getValue().toString()),Double.parseDouble(dataSnapshot.child("1").child("lng").getValue().toString()));
                one.num="1";
                two.loc=new LatLng(Double.parseDouble(dataSnapshot.child("1").child("lat").getValue().toString()),Double.parseDouble(dataSnapshot.child("1").child("lng").getValue().toString()));
                two.num="2";
                three.loc=new LatLng(Double.parseDouble(dataSnapshot.child("1").child("lat").getValue().toString()),Double.parseDouble(dataSnapshot.child("1").child("lng").getValue().toString()));
                three.num="3";
                for(DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        if(distance(three.loc.latitude,three.loc.longitude,cur_location.getLatitude(),cur_location.getLongitude())>
                                distance(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()),cur_location.getLatitude(),cur_location.getLongitude()))
                        {
                            if(distance(two.loc.latitude,two.loc.longitude,cur_location.getLatitude(),cur_location.getLongitude())>
                                    distance(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()),cur_location.getLatitude(),cur_location.getLongitude()))
                            {
                                if(distance(one.loc.latitude,one.loc.longitude,cur_location.getLatitude(),cur_location.getLongitude())>
                                        distance(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()),cur_location.getLatitude(),cur_location.getLongitude()))
                                {
                                    one.loc=new LatLng(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()));
                                    one.num=ds.child("id_no").getValue().toString();
                                }
                                else
                                {
                                    two.loc=new LatLng(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()));
                                    two.num=ds.child("id_no").getValue().toString();

                                }
                            }
                            else
                            {
                                three.loc=new LatLng(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()));
                                three.num=ds.child("id_no").getValue().toString();
                            }
                        }
                    }
                    toilets=new ArrayList<Institution>();
                    toilets.add(one);
                    toilets.add(two);
                    toilets.add(three);

                ////Trash data
                myRef.child("Garbage").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        one.loc=new LatLng(Double.parseDouble(dataSnapshot.child("1").child("lat").getValue().toString()),Double.parseDouble(dataSnapshot.child("1").child("lng").getValue().toString()));
                        one.num="1";
                        two.loc=new LatLng(Double.parseDouble(dataSnapshot.child("2").child("lat").getValue().toString()),Double.parseDouble(dataSnapshot.child("1").child("lng").getValue().toString()));
                        two.num="2";
                        three.loc=new LatLng(Double.parseDouble(dataSnapshot.child("3").child("lat").getValue().toString()),Double.parseDouble(dataSnapshot.child("1").child("lng").toString()));
                        three.num="3";
                        for(DataSnapshot ds:dataSnapshot.getChildren())
                        {
                            if(distance(three.loc.latitude,three.loc.longitude,cur_location.getLatitude(),cur_location.getLongitude())>
                                    distance(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").getValue().toString()),cur_location.getLatitude(),cur_location.getLongitude()))
                            {
                                if(distance(two.loc.latitude,two.loc.longitude,cur_location.getLatitude(),cur_location.getLongitude())>
                                        distance(Double.parseDouble(ds.child("lat").getValue().toString()),Double.parseDouble(ds.child("lng").toString()),cur_location.getLatitude(),cur_location.getLongitude()))
                                {
                                    if(distance(one.loc.latitude,one.loc.longitude,cur_location.getLatitude(),cur_location.getLongitude())>
                                            distance(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()),cur_location.getLatitude(),cur_location.getLongitude()))
                                    {
                                        one.loc=new LatLng(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()));
                                        one.num=ds.child("id_no").getValue().toString();
                                    }
                                    else
                                    {
                                        two.loc=new LatLng(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()));
                                        two.num=ds.child("id_no").getValue().toString();
                                    }
                                }
                                else
                                {
                                    three.loc=new LatLng(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()));
                                    three.num=ds.child("id_no").getValue().toString();
                                }
                            }
                        }
                        garbage=new ArrayList<Institution>();
                        garbage.add(one);
                        garbage.add(two);
                        garbage.add(three);

                        /////Health care data
                        myRef.child("Healthcare").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                one.loc=new LatLng(Double.parseDouble(dataSnapshot.child("1").child("lat").toString()),Double.parseDouble(dataSnapshot.child("1").child("lng").toString()));
                                one.num="1";
                                two.loc=new LatLng(Double.parseDouble(dataSnapshot.child("1").child("lat").toString()),Double.parseDouble(dataSnapshot.child("1").child("lng").toString()));
                                two.num="2";
                                three.loc=new LatLng(Double.parseDouble(dataSnapshot.child("1").child("lat").toString()),Double.parseDouble(dataSnapshot.child("1").child("lng").toString()));
                                three.num="3";
                                for(DataSnapshot ds:dataSnapshot.getChildren())
                                {
                                    Log.d("firebase checking",ds.child("lat").toString());
                                    if(distance(three.loc.latitude,three.loc.longitude,cur_location.getLatitude(),cur_location.getLongitude())>
                                            distance(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()),cur_location.getLatitude(),cur_location.getLongitude()))
                                    {
                                        if(distance(two.loc.latitude,two.loc.longitude,cur_location.getLatitude(),cur_location.getLongitude())>
                                                distance(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()),cur_location.getLatitude(),cur_location.getLongitude()))
                                        {
                                            if(distance(one.loc.latitude,one.loc.longitude,cur_location.getLatitude(),cur_location.getLongitude())>
                                                    distance(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()),cur_location.getLatitude(),cur_location.getLongitude()))
                                            {
                                                one.loc=new LatLng(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()));
                                                one.num=ds.child("id_no").getValue().toString();
                                            }
                                            else
                                            {
                                                two.loc=new LatLng(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()));
                                                two.num=ds.child("id_no").getValue().toString();
                                            }
                                        }
                                        else
                                        {
                                            three.loc=new LatLng(Double.parseDouble(ds.child("lat").toString()),Double.parseDouble(ds.child("lng").toString()));
                                            three.num=ds.child("id_no").getValue().toString();
                                        }
                                    }
                                }
                                toilets=new ArrayList<Institution>();
                                toilets.add(one);
                                toilets.add(two);
                                toilets.add(three);
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
            ImageView rdp = (CircleImageView) mrker.findViewById(R.id.insti_dp);
            rdp.setImageResource(R.drawable.toilet);
            LatLng ll = toilets.get(i).loc;
            MarkerOptions options = new MarkerOptions().title("Toilet").snippet(toilets.get(i).num).position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            mMap.addMarker(options);

            ////////////
            rdp.setImageResource(R.drawable.trash);
            ll = garbage.get(i).loc;
            options = new MarkerOptions().title("Garbage").snippet(garbage.get(i).num).position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            mMap.addMarker(options);

            /////////////
            rdp.setImageResource(R.drawable.healthcare);
            ll = healthcare.get(i).loc;
            options = new MarkerOptions().title("Healthcare").snippet(healthcare.get(i).num).position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
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
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
            cur_location=location;
            Toast.makeText(MapsActivity.this,"Location changed "+cur_location,LENGTH_LONG).show();
           // setMyMarker(location);

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
