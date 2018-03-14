package com.wisdom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    int num,tot_comments,rating;
    int curr_point;
    boolean commented,commenting;
    Locations.Comments my_comment=null;
    ListView listView;
    String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_content);
        Bundle extras = getIntent().getExtras();
        uid="1";
        String value1 = extras.getString(Intent.EXTRA_TEXT);
        ///
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
                EditText comment=(EditText)addcomment.findViewById(R.id.editText2);
                SmileRating sr=(SmileRating)addcomment.findViewById(R.id.smile_rating);
                if(commenting==false)
                    {
                        commenting=true;
                        addcomment.setVisibility(View.VISIBLE);
                        commentlist.setVisibility(View.INVISIBLE);
                        if(commented==true)
                        {
                            comment.setText(my_comment.text);
                            int ratenum = 0;
                            switch(my_comment.rating)
                            {
                                case "terrible":     ratenum=1;
                                    break;

                                case "bad":     ratenum=2;
                                    break;

                                case "okay":     ratenum=3;
                                    break;

                                case "good":     ratenum=4;
                                    break;

                                case "great":     ratenum=5;
                                    break;
                            }

                            sr.setSelectedSmile(ratenum);
                        }
                    }
                    else
                    {
                        commenting=false;
                        addcomment.setVisibility(View.INVISIBLE);
                        commentlist.setVisibility(View.VISIBLE);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("");
                        myRef.child("Comments").child(num+"").child(uid).child("text").setValue(comment.getText().toString());
                        String rate_name = "";
                        switch (sr.getRating())
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
                        myRef.child("Comments").child(num+"").child(uid).child("rating").setValue(rate_name);
                        myRef.child("Comments").child(num+"").child(uid).child("person").setValue(uid);
                        setComments();
                    }
                    listView.removeAllViews();

            }
        });
        setDetails();
        setComments();
    }
    void setDetails()
    {
        TextView addrss_tv=(TextView)findViewById(R.id.address_name);
        TextView type_tv=(TextView)findViewById(R.id.type);
        TextView name_tv=(TextView)findViewById(R.id.address_name);
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
        myRef.child("Comments").child(num+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                    for(DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        if(ds.child("uid").getValue().toString().equals(uid))
                        {
                            commented=true;
                            my_comment.text=ds.child("uid").getValue().toString();
                            my_comment.rating=ds.child("uid").getValue().toString();
                            button.setText("Edit Comment");
                            break;
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


    public void setComments()
    {
        final ArrayList<Locations.Comments> comments=new ArrayList<Locations.Comments>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        myRef.child("Comments").child(num+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int i=0;
                listView=findViewById(R.id.comments);
                for(i=0;i<comments.size();++i)
                    comments.remove(i);
                i=0;
                final DataSnapshot finds=dataSnapshot;
                for(final DataSnapshot ds:dataSnapshot.getChildren())
                {
                    i++;
                    final Locations.Comments cmmnts=new Locations.Comments();
                    cmmnts.rating=ds.child("rating").getValue().toString();
                    cmmnts.text=ds.child("text").getValue().toString();
                    final int finalI = i;
                    myRef.child("Users").child(ds.child("person").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            cmmnts.name=dataSnapshot.child("name").getValue().toString();
                            cmmnts.dp =dataSnapshot.child("dp_uri").getValue().toString();
                            comments.add(cmmnts);
                            if(finalI ==finds.getChildrenCount())
                            {
                                CommentAdapter commentAdapter=new CommentAdapter(LocationDetailer.this,comments);
                                listView.setAdapter(commentAdapter);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                    Log.d("Comments adapt:", cmmnts.rating+"   "+ cmmnts.text+"  ");
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