package com.wisdom;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

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
    Marker my_marker=null;
    GoogleMap mMap=null,statmMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationListenSet();
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        statmMap = mMap;
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
