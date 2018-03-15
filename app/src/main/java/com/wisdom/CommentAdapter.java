package com.wisdom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by User on 22-Feb-18.
 */

public class CommentAdapter extends BaseAdapter {
    ArrayList<Locations.Comments> commnts;
    String[] reaction={"terrible","bad","okay","good","great"};
    Context context;
    LayoutInflater inflater=null;
CommentAdapter(Context context, ArrayList<Locations.Comments> commnts)
{
    this.context=context;
    this.commnts=commnts;
    inflater = (LayoutInflater)context.
            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
}
    @Override
    public int getCount() {
        return commnts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view=inflater.inflate(R.layout.comment_list,null);
        final TextView name=(TextView)view.findViewById(R.id.cmmntr_name);
        final TextView comment=(TextView)view.findViewById(R.id.comment);
        final CircleImageView c_dp=(CircleImageView)view.findViewById(R.id.comment_dp);
        comment.setSelected(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Glide.with(context).load(commnts.get(position).dp).into(c_dp);
        name.setText(commnts.get(position).name);
        comment.setText(commnts.get(position).text);
        ImageView reactn=(ImageView)view.findViewById(R.id.reaction);
        int resID = context.getResources().getIdentifier(reaction[commnts.get(position).rating-1]
                ,"drawable", "com.wisdom");
        reactn.setImageResource(resID );

        return view;
    }
}
