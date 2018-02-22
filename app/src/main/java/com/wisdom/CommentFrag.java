package com.wisdom;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hsalf.smilerating.SmileRating;

/**
 * Created by User on 23-Feb-18.
 */

@SuppressLint("ValidFragment")
public class CommentFrag extends Fragment {
    View view;
    Context context;
    String type;
    @SuppressLint("ValidFragment")
    CommentFrag(Context context, String type)
    {
        this.context=context;
        this.type=type;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.view=inflater.inflate(R.layout.addcomment,container,false);
        Button done=(Button)view.findViewById(R.id.frag_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText commnt=(EditText)view.findViewById(R.id.editText2);
                SmileRating rating=(SmileRating)view.findViewById(R.id.smile_rating);
                int rate=rating.getRating();
                String comment=commnt.getText().toString();
            }
        });
        return view;
    }
}
