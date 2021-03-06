package com.wisdom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hsalf.smilerating.SmileRating;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by User on 13-Mar-18.
 */

public class LocationDetailer extends AppCompatActivity
{

    String type,name,address;
    private static final String PREF_FILE = "UserPreferences";

    int num,tot_comments,rating;
    int curr_point,comm_my_pos;
    boolean commented,listed,commenting;
    Locations.Comments my_comment=null;
    ListView listView;
    private Animator mCurrentAnimator;
    int mShortAnimationDuration;
    CommentAdapter commentAdapter;
    AlertDialog alertDialog;
    String uid;
    ArrayList<Locations.Comments> comments;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        my_comment=new Locations.Comments();
        listed=false;
        alertDialog = new AlertDialog.Builder(
                LocationDetailer.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Login please");

        // Setting Dialog Message
        alertDialog.setMessage("You need to login for comment about the service");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.cantcomment);

        // Setting OK Button
        alertDialog.setButton("Not now",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        alertDialog.setButton("Login",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent login=new Intent(LocationDetailer.this,SplashActivity.class);
                startActivity(login);
            }

        });
        // Showing Alert Message
        setContentView(R.layout.bottom_sheet_content);
        Bundle extras = getIntent().getExtras();
        SharedPreferences user_uid=getSharedPreferences(PREF_FILE,MODE_PRIVATE);
        uid=user_uid.getString("user_id","0");
        comm_my_pos=-1;
        String value1 = extras.getString(Intent.EXTRA_TEXT);
        comments=new ArrayList<Locations.Comments>();
        type=extras.getString("loc type");
        name=extras.getString("loc name");
        commenting=false;
        address=extras.getString("loc address");
        rating=extras.getInt("loc rating");
        tot_comments=extras.getInt("loc tot_comments");
        final View addcomment=findViewById(R.id.add_comment);
        final View commentlist=findViewById(R.id.comment_list);
        commentlist.setVisibility(View.VISIBLE);
        addcomment.setVisibility(View.INVISIBLE);
        curr_point=tot_comments;
        num=extras.getInt("loc num");
        final Button button=(Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(commenting==false)
                    {
                        SmileRating sr = (SmileRating) addcomment.findViewById(R.id.smile_rating);
                        sr.setSelectedSmile(3);
                        if(uid.equals("0"))
                        {
                            alertDialog.show();
                        }
                        else {
                            commenting = true;
                            addcomment.setVisibility(View.VISIBLE);
                            Button entercomment = (Button) addcomment.findViewById(R.id.entercomment);
                            entercomment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final boolean[] rated = {false};
                                    EditText comment = (EditText) addcomment.findViewById(R.id.editText2);
                                    SmileRating sr = (SmileRating) addcomment.findViewById(R.id.smile_rating);

                                    if (comment.getText().toString().equals("")) {
                                        Toast.makeText(LocationDetailer.this, "Enter a comment", Toast.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(LocationDetailer.this, "Thank you", Toast.LENGTH_LONG).show();
                                        int rating = sr.getRating();
                                        if (rating == 0)
                                            rating = 1;
                                        final ProgressBar progressBar = findViewById(R.id.comment_progress);
                                        progressBar.setVisibility(View.VISIBLE);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        final DatabaseReference myRef = database.getReference("");
                                        myRef.child("Comments").child(num + "").child(uid).child("text").setValue(comment.getText().toString());
                                        myRef.child("Comments").child(num + "").child(uid).child("rating").setValue(rating);
                                        myRef.child("Comments").child(num + "").child(uid).child("person").setValue(uid);
                                        myRef.child("Comments").child(num + "").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int i = 0;
                                                float sum = 0, avg;
                                                final DataSnapshot finds = dataSnapshot;
                                                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    ++i;
                                                    sum += Integer.parseInt(ds.child("rating").getValue().toString());
                                                }
                                                avg = sum / i;
                                                avg = (float) (avg + 0.5);
                                                int rate = (int) avg;
                                                myRef.child("Locations").child(num + "").child("rating").setValue(rate);
                                                commenting = false;
                                                addcomment.setVisibility(View.INVISIBLE);
                                                commentlist.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                //  comments = new ArrayList<Locations.Comments>();
                                                //    if(commentAdapter!=null)
                                                //      commentAdapter.notifyDataSetChanged();
                                                setComments();
                                                checkCommented();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                // Failed to read value
                                                Log.w(TAG, "Failed to read value.", error.toException());
                                            }
                                        });
                                    }
                                }
                            });
                            commentlist.setVisibility(View.INVISIBLE);
                            if (commented == true) {
                                EditText comment = (EditText) addcomment.findViewById(R.id.editText2);
                                comment.setText(my_comment.text);
                                sr.setSelectedSmile(my_comment.rating);
                            }
                        }

                    }
                    else
                    {
                       commenting=true;
                    }


            }
        });
        setDetails();
        if(!uid.equals(0))
         setComments();
        setDescription();
        setRating();
        checkCommented();
    }
    void setRating()
    {
        ImageView rating_iv=(ImageView)findViewById(R.id.rating);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        myRef.child("Locations").child(num+"").child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String rate_name = "";
                switch (Integer.parseInt(dataSnapshot.getValue().toString()))
                {
                    case 1:     rate_name="terrible";
                        break;

                    case 2:     rate_name="bad";
                        break;

                    case 3:     rate_name="okay";
                        break;

                    case 4:     rate_name="good";
                        break;

                    case 5:     rate_name="great";
                        break;

                    default:    rate_name="norating";
                }

                String uri = "@drawable/";  // where myresource (without the extension) is the file

                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                ImageView imageview= (ImageView)findViewById(R.id.rating);
                Drawable drawable = getResources().getDrawable(getResources().getIdentifier(rate_name, "drawable", getPackageName()));
                imageview.setImageDrawable(drawable);



            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }
    void setDetails()
    {
        TextView addrss_tv=(TextView)findViewById(R.id.address_name);
        TextView type_tv=(TextView)findViewById(R.id.type);
        TextView name_tv=(TextView)findViewById(R.id.name);
        ImageView rating_iv=(ImageView)findViewById(R.id.rating);

        addrss_tv.setText(address);
        type_tv.setText(type);
        name_tv.setText(name);
        String rate_name = "";
        switch (rating)
        {
            case 1:     rate_name="terrible";
                        break;

            case 2:     rate_name="bad";
                        break;

            case 3:     rate_name="okay";
                        break;

            case 4:     rate_name="good";
                        break;

            case 5:     rate_name="great";
                        break;
        }

        String uri = "@drawable/";  // where myresource (without the extension) is the file

        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        ImageView imageview= (ImageView)findViewById(R.id.rating);
        Drawable drawable = getResources().getDrawable(getResources().getIdentifier(rate_name, "drawable", getPackageName()));
        imageview.setImageDrawable(drawable);
    }
    void  setDescription()
    {
        final TextView description=(TextView)findViewById(R.id.description);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        myRef.child("Locations").child(num+"").child("description").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                description.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    void checkCommented()
    {
        final Button button=(Button)findViewById(R.id.button2);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        myRef.child("Comments").child(num+"").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                    if(dataSnapshot.child("text").getValue()!=null)
                    {
                        my_comment=new Locations.Comments();
                        commented=true;
                        my_comment.text=dataSnapshot.child("text").getValue().toString();
                        my_comment.rating=Integer.parseInt(dataSnapshot.child("rating").getValue().toString())-1;
                        button.setText("Edit Comment");
                    }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    public void setComments()
    {
        final ProgressBar progressBar=findViewById(R.id.comment_progress);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        myRef.child("Comments").child(num+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int i=0;
                listView=findViewById(R.id.comments);
                i=0;
                final DataSnapshot finds=dataSnapshot;
                View cmmlist=findViewById(R.id.comments);
                View cmmtv=findViewById(R.id.comment_tv);

                View nocomments=findViewById(R.id.nocomments);
                if(dataSnapshot.getChildrenCount()==0)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    nocomments.setVisibility(View.VISIBLE);
                    cmmlist.setVisibility(View.INVISIBLE);
                    cmmtv.setVisibility(View.INVISIBLE);
                }
                else
                {
                    if(cmmlist.getVisibility()==View.INVISIBLE)
                    {

                        nocomments.setVisibility(View.INVISIBLE);
                        cmmlist.setVisibility(View.VISIBLE);
                        cmmtv.setVisibility(View.VISIBLE);

                    }
                    comments.clear();
                    for (final DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        final Locations.Comments cmmnts=new Locations.Comments();
                        i++;
                        cmmnts.rating = Integer.parseInt(ds.child("rating").getValue().toString());
                        cmmnts.text = ds.child("text").getValue().toString();
                        final int finalI = i;
                        myRef.child("Users").child(ds.child("person").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                cmmnts.name = dataSnapshot.child("name").getValue().toString();
                                cmmnts.dp = dataSnapshot.child("dp_uri").getValue().toString();
                                comments.add(cmmnts);
                                if(uid.equals(dataSnapshot.child("dp_uri").getValue().toString()))
                                    comm_my_pos=comments.size();
                                if (finalI == finds.getChildrenCount()) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if (listed == false) {
                                        commentAdapter = new CommentAdapter(LocationDetailer.this, comments);
                                        listView.setAdapter(commentAdapter);
                                    }
                                    else
                                    {
                                        ((CommentAdapter) listView.getAdapter()).notifyDataSetChanged();
                                      //  commentAdapter=new CommentAdapter(LocationDetailer.this,comments);
                                        Toast.makeText(LocationDetailer.this,"Comment added",Toast.LENGTH_LONG).show();
                                        commentAdapter.notifyDataSetChanged();
                                    }
                                    listed = true;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


}