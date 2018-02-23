package com.wisdom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smilerating.SmileRating;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by User on 22-Feb-18.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    public LocationManager mLocationManager = null;
    SupportMapFragment mapFragment;
    BottomSheetBehavior<View> mBottomSheetBehavior1;
    String cur_typ;
    LatLng cur_location=null;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        cur_location=new LatLng(9.7433562,76.368284);
        new Thread(new Runnable() {
            public void run() {
                progressBar.dismiss();
                retrieveTheNearServices();
                Log.d("firebase checking","yaa fine");
            }
        }).start();

        Button intnt_btn = findViewById(R.id.intnt_btn);
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
        setMyMarker(cur_location);
        bottomSheet = findViewById(R.id.btm_sheet);
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
                mBottomSheetBehavior1.setPeekHeight(450);
                final View bottomSheet = findViewById(R.id.btm_sheet);
                TextView address = bottomSheet.findViewById(R.id.address_name);
                address.setSelected(true);
                TextView type = bottomSheet.findViewById(R.id.type);
                LinearLayout imgs = bottomSheet.findViewById(R.id.imgs);
                SmileRating sr = bottomSheet.findViewById(R.id.smile_rating);
                sr.setClickable(false);
                String imageName = "";
                Button addcomnt = bottomSheet.findViewById(R.id.button2);
                ListView comments = bottomSheet.findViewById(R.id.comments);
                addcomnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        View btm_review = findViewById(R.id.btm_review);
                            btm_review.setVisibility(View.VISIBLE);
                        View btm_sheet = findViewById(R.id.btm_profile);
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
                    ImageView image = rView.findViewById(R.id.insti_img);
                    image.setImageResource(resID );
                    imgs.addView(rView);
                }

                /////////////////////////////
                Button setcomnt = bottomSheet.findViewById(R.id.frag_done);
                setcomnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText commnt = bottomSheet.findViewById(R.id.editText2);
                        SmileRating rating = bottomSheet.findViewById(R.id.smile_rating);
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
                        View btm_review = findViewById(R.id.btm_review);
                        btm_review.setVisibility(View.INVISIBLE);
                        View btm_sheet = findViewById(R.id.btm_profile);
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
                        one.address=ds.child("address").getValue().toString();
                        one.rate=Integer.parseInt(ds.child("rating").getValue().toString());

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
                            one.address=ds.child("address").getValue().toString();
                            one.type="Garbage";
                            one.rate=Integer.parseInt(ds.child("rating").getValue().toString());
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
                                    one.address=ds.child("address").getValue().toString();
                                    one.type="Healthcare";
                                    one.rate=Integer.parseInt(ds.child("rating").getValue().toString());
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
            ImageView rdp = mrker.findViewById(R.id.insti_dp);
            rdp.setImageResource(R.drawable.toilet);
            LatLng ll = toilets.get(i).loc;
            MarkerOptions options = new MarkerOptions().title("Toilet").snippet(i+"").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            mMap.addMarker(options);

            ////////////
            rdp.setImageResource(R.drawable.trash);
            ll = garbage.get(i).loc;
            options = new MarkerOptions().title("Garbage").snippet(i+"").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            mMap.addMarker(options);

            /////////////
            rdp.setImageResource(R.drawable.healthcare);
            ll = healthcare.get(i).loc;
            options = new MarkerOptions().title("Healthcare").snippet(i+"").position(ll).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, mrker)));
            mMap.addMarker(options);
        }
        Log.d("firebase checking","Markers are the set");

    }

       void setMyMarker(final LatLng loc)
       {
            SharedPreferences locpref= getSharedPreferences("UserDetails", MODE_PRIVATE);
            View mrker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);
           final CircleImageView rdp = mrker.findViewById(R.id.imageView1);
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

    public void changeCam(LatLng ll)
    {
        CameraUpdate location= CameraUpdateFactory.newLatLngZoom(ll,15);
        mMap.animateCamera(location);

    }
}
