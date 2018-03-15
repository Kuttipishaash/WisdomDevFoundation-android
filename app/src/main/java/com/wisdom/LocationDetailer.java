package com.wisdom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
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
    int num,tot_comments,rating;
    int curr_point;
    boolean commented,commenting;
    Locations.Comments my_comment=null;
    ListView listView;
    private Animator mCurrentAnimator;
    int mShortAnimationDuration;
    CommentAdapter commentAdapter;
    String uid;
    ArrayList<Locations.Comments> comments;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_content);
        Bundle extras = getIntent().getExtras();
        uid="1";
        String value1 = extras.getString(Intent.EXTRA_TEXT);
        ///
         mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
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
                        commenting=true;
                        addcomment.setVisibility(View.VISIBLE);
                        Button entercomment=(Button)addcomment.findViewById(R.id.entercomment);
                        entercomment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                final ProgressBar progressBar=findViewById(R.id.comment_progress);
                                progressBar.setVisibility(View.VISIBLE);
                                EditText comment=(EditText)addcomment.findViewById(R.id.editText2);
                                SmileRating sr=(SmileRating)addcomment.findViewById(R.id.smile_rating);
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference myRef = database.getReference("");
                                myRef.child("Comments").child(num+"").child(uid).child("text").setValue(comment.getText().toString());
                                myRef.child("Comments").child(num+"").child(uid).child("rating").setValue(sr.getRating());
                                myRef.child("Comments").child(num+"").child(uid).child("person").setValue(uid);
                                myRef.child("Comments").child(num+"").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        int i=0;
                                        float sum=0,avg;
                                        final DataSnapshot finds=dataSnapshot;
                                        for(final DataSnapshot ds:dataSnapshot.getChildren())
                                        {
                                            ++i;
                                            sum+=Integer.parseInt(ds.child("rating").getValue().toString());
                                        }
                                        avg=sum/i;
                                        avg= (float) (avg+0.5);
                                        int rate= (int) avg;
                                        myRef.child("Locations").child(num+"").child("rating").setValue(rate);
                                        commenting=false;
                                        addcomment.setVisibility(View.INVISIBLE);
                                        commentlist.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        comments=new ArrayList<Locations.Comments>();
                                        commentAdapter.notifyDataSetChanged();
                                        setComments();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
                            }
                        });
                        commentlist.setVisibility(View.INVISIBLE);
                        if(commented==true)
                        {
                            EditText comment=(EditText)addcomment.findViewById(R.id.editText2);
                            SmileRating sr=(SmileRating)addcomment.findViewById(R.id.smile_rating);
                            comment.setText(my_comment.text);
                            sr.setSelectedSmile(my_comment.rating);
                        }
                    }
                    else
                    {
                       commenting=true;
                    }

            }
        });
        setDetails();
        setComments();
        setDescription();
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
                            my_comment.rating=Integer.parseInt(ds.child("uid").getValue().toString());
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
    public void setImages()
    {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("");
        myRef.child("Locations").child(num+"").child("num_img").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int imgnum=0;
                String filename;
                imgnum=Integer.parseInt(dataSnapshot.getValue().toString());
                for(int i=1;i<imgnum+1;++i)
                {
                    filename=num+"_"+i;
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    storageRef.child("place pictures/+"+filename+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                           Uri imagepath=uri;
                            LinearLayout requestView = (LinearLayout) findViewById(R.id.imgs);
                            LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rView = inflater.inflate(R.layout.pager_item, null);
                            ImageView placeimg=(ImageView)rView.findViewById(R.id.placeimg);
                            Glide.with(LocationDetailer.this).load(uri).into(placeimg);
                            rView.setTag(uri+"");
                            requestView.addView(rView);

                            rView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String uri= (String) v.getTag();
                                //    zoomImageFromThumb(rView, uri);

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

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
                for(final DataSnapshot ds:dataSnapshot.getChildren())
                {
                    i++;
                    final Locations.Comments cmmnts=new Locations.Comments();
                    cmmnts.rating=Integer.parseInt(ds.child("rating").getValue().toString());
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
                                progressBar.setVisibility(View.INVISIBLE);
                                commentAdapter=new CommentAdapter(LocationDetailer.this,comments);
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
  /*  private void zoomImageFromThumb(final View thumbView, String uri) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        Glide.with(LocationDetailer.this).load(uri).into(expandedImageView);
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }*/


}