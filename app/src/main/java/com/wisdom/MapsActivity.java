package com.wisdom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.hsalf.smilerating.SmileRating;
import com.mahc.custombottomsheetbehavior.BottomSheetBehaviorGoogleMapsLike;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;

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
    String cur_typ;
    ArrayList<Integer> lwr_activities=new ArrayList<Integer>();
    ArrayList<Integer> upr_activities=new ArrayList<Integer>();

    LatLng cur_location=null;
    LatLng destination=null;
    ArrayList<Institution> toilets=new ArrayList<Institution>();
    ArrayList<Institution> garbage=new ArrayList<Institution>();
    ArrayList<Institution> healthcare=new ArrayList<Institution>();
    ArrayList<Locations> locations=new ArrayList<Locations>();
    View bottomSheet;
    String[] person_name={"Kuttapan","Shaji","Bilal","Jose","Prabhakaran","Romy","Enthiran","Tonikutan"};
    String[] person_comment={"Havent seen such a beutiful place",
                             "Top notch all the way",
                            "Cant believe how come everbody here is so nice",
                            "Found a dead cockroach inside there...",
                            "One of immemorable experience in my life","Want to visit again",
                            "Loved this place...",
                            "Worth visiting",
                            "Great service..",
                            "Everyone should visit here once in life",
                            "liked so much <3 <3"};
    String[] dp_name={"a","b","c","d","e","f","g"};
    Marker my_marker=null;
    Institution one,two,three;
    CommentAdapter commentLister = null;
    GoogleMap mMap=null,statmMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        startService(new Intent(getBaseContext(), LocationService.class));
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
        cur_location=null;
        new Thread(new Runnable() {
            public void run() {
                while (locpref.getString("lat","").equals("")||cur_location==null);
                progressBar.dismiss();
               // retrieveTheNearServices();
                Log.d("firebase checking","yaa fine");
            }
        }).start();

        Button intnt_btn=(Button)findViewById(R.id.intnt_btn);
        intnt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MapsActivity.this,NewsFeedActivity.class);
                startActivity(intent);
            }
        });


    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Toast.makeText(this,"map ready",LENGTH_LONG).show();
        mMap = googleMap;
        statmMap = mMap;
        //setMyMarker(cur_location);
        drawRoute();
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
            public boolean onMarkerClick(final Marker marker) {
                BottomSheetBehavior mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                final View bottomSheet = findViewById(R.id.btm_sheet);
                TextView address=(TextView)bottomSheet.findViewById(R.id.address_name);
                address.setSelected(true);
                TextView type=(TextView)bottomSheet.findViewById(R.id.type);
                LinearLayout imgs=(LinearLayout)bottomSheet.findViewById(R.id.imgs);

                SmileRating sr=(SmileRating)bottomSheet.findViewById(R.id.smile_rating);
                sr.setClickable(false);
                String imageName = "";
                Button addcomnt=(Button)bottomSheet.findViewById(R.id.button2);
                ListView comments=(ListView)bottomSheet.findViewById(R.id.comments);
                addcomnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                            View btm_review=(View)findViewById(R.id.btm_review);
                            btm_review.setVisibility(View.VISIBLE);
                            View btm_sheet=(View)findViewById(R.id.btm_sheet);
                            btm_sheet.setVisibility(View.INVISIBLE);
                    }
                });
                if(marker.getTitle().equals("Toilet"))
                {
                    cur_typ="T";
                    imageName="toilet";
                    address.setText(toilets.get(Integer.parseInt(marker.getSnippet())).address);
                    type.setText(marker.getTitle());
                    sr.setSelectedSmile(healthcare.get(Integer.parseInt(marker.getSnippet())).rate);
                    for(int i=0;i<4;++i)
                    {
                        Institution.CommentsRating one=new Institution.CommentsRating();
                        one.name=person_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        one.comment=person_comment[ThreadLocalRandom.current().nextInt(0, 10)];
                        one.dp=dp_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        toilets.get(Integer.parseInt(marker.getSnippet())).cmmnts.add(one);
                    }
                    commentLister=new CommentAdapter(MapsActivity.this,toilets.get(Integer.parseInt(marker.getSnippet())).cmmnts);
                    comments.setAdapter(commentLister);

                }
                else if(marker.getTitle().equals("Garbage"))
                {
                    cur_typ="G";
                    imageName="garbage";
                    address.setText(garbage.get(Integer.parseInt(marker.getSnippet())).address);
                    type.setText(marker.getTitle());
                    sr.setSelectedSmile(healthcare.get(Integer.parseInt(marker.getSnippet())).rate);
                    for(int i=0;i<4;++i)
                    {
                        Institution.CommentsRating one=new Institution.CommentsRating();
                        one.name=person_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        one.comment=person_comment[ThreadLocalRandom.current().nextInt(0, 10)];
                        one.dp=dp_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        garbage.get(Integer.parseInt(marker.getSnippet())).cmmnts.add(one);
                    }
                    commentLister=new CommentAdapter(MapsActivity.this,garbage.get(Integer.parseInt(marker.getSnippet())).cmmnts);
                    comments.setAdapter(commentLister);
                }
                else if(marker.getTitle().equals("Healthcare"))
                {
                    cur_typ="H";
                    imageName="healthclinic";
                    address.setText(healthcare.get(Integer.parseInt(marker.getSnippet())).address);
                    type.setText(marker.getTitle());
                    for(int i=0;i<4;++i)
                    {
                        Institution.CommentsRating one=new Institution.CommentsRating();
                        one.name=person_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        one.comment=person_comment[ThreadLocalRandom.current().nextInt(0, 10)];
                        one.dp=dp_name[ThreadLocalRandom.current().nextInt(0, 7)];
                        healthcare.get(Integer.parseInt(marker.getSnippet())).cmmnts.add(one);
                    }
                    commentLister=new CommentAdapter(MapsActivity.this,healthcare.get(Integer.parseInt(marker.getSnippet())).cmmnts);
                    comments.setAdapter(commentLister);
                    sr.setSelectedSmile(healthcare.get(Integer.parseInt(marker.getSnippet())).rate);
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

                /////////////////////////////
                Button setcomnt=(Button)bottomSheet.findViewById(R.id.frag_done);
                setcomnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText commnt=(EditText)bottomSheet.findViewById(R.id.editText2);
                        SmileRating rating=(SmileRating)bottomSheet.findViewById(R.id.smile_rating);
                        int rate=rating.getRating();
                        String comment=commnt.getText().toString();
                        Institution.CommentsRating one=new Institution.CommentsRating();
                        one.rate=rate;
                        one.comment=comment;
                        if(cur_typ.equals("T"))
                            toilets.get(Integer.parseInt(marker.getSnippet())).cmmnts.add(one);
                        else if(cur_typ.equals("G"))
                            garbage.get(Integer.parseInt(marker.getSnippet())).cmmnts.add(one);
                        else if(cur_typ.equals("H"))
                            healthcare.get(Integer.parseInt(marker.getSnippet())).cmmnts.add(one);
                        commentLister.notifyDataSetChanged();
                        View btm_review=(View)findViewById(R.id.btm_review);
                        btm_review.setVisibility(View.INVISIBLE);
                        View btm_sheet=(View)findViewById(R.id.btm_sheet);
                        btm_sheet.setVisibility(View.VISIBLE);
                    }
                });



                return false;
            }
        });
    }


    void retrieveTheNearServices()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        /////Toilet data
        myRef.child("Locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int i=0;
                for(DataSnapshot ds:dataSnapshot.getChildren())
                    {
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

                            myRef.child("Comments").child(loctn.num+"").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot==null)
                                        loctn.tot_comments=0;
                                    else
                                        loctn.tot_comments=(int) dataSnapshot.getChildrenCount();
                                    locations.add(loctn);
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });
                        }
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
    void drawRoute()
    {
        //String url = getDirectionsUrl(cur_location,destination);
        String url = getDirectionsUrl(cur_location,destination);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }
    private String getDirectionsUrl(LatLng origin,LatLng dest){
        origin=new LatLng(9.993421,76.358412);
        dest=new LatLng(10.033090,76.295216);
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GREEN);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
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
            storageReference.child("display pictures/"+locations.get(i).type+".png").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                    rdp.setImageBitmap(bitmap);
                    LatLng ll = locations.get(finalI).loc;
                    MarkerOptions options = new MarkerOptions().title(locations.get(finalI).type).snippet(finalI +"").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
                    mMap.addMarker(options);
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
            SharedPreferences locpref= getSharedPreferences("UserDetails", MODE_PRIVATE);
            View mrker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
            final CircleImageView rdp = (CircleImageView) mrker.findViewById(R.id.imageView1);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultdp);
            rdp.setImageBitmap(icon);
            LatLng ll = new LatLng(loc.latitude,loc.longitude);
            MarkerOptions options = new MarkerOptions().title("ME").snippet("HAHA").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            mMap.addMarker(options);
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
            Toast.makeText(MapsActivity.this,"Location changed "+cur_location,LENGTH_LONG).show();
            setMyMarker(cur_location);
            retrieveTheNearServices();
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
}
