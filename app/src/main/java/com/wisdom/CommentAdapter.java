package com.wisdom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 22-Feb-18.
 */

public class CommentAdapter extends BaseAdapter {
    ArrayList<Institution.CommentsRating> commnts;
    String[] reaction={"good","great","okay","bad","terrible"};
    Context context;
    LayoutInflater inflater=null;
CommentAdapter(Context context, ArrayList<Institution.CommentsRating> commnts)
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=inflater.inflate(R.layout.comment_list,null);
        TextView name=(TextView)view.findViewById(R.id.cmmntr_name);
        TextView comment=(TextView)view.findViewById(R.id.comment);
        comment.setSelected(true);
        name.setText(commnts.get(position).name);
        comment.setText(commnts.get(position).comment);
        ImageView reactn=(ImageView)view.findViewById(R.id.reaction);
        CircleImageView c_dp=(CircleImageView)view.findViewById(R.id.comment_dp);
        int resID = context.getResources().getIdentifier(reaction[ThreadLocalRandom.current().nextInt(0, 5)]
                ,"drawable", "com.wisdom");
        reactn.setImageResource(resID );
        resID = context.getResources().getIdentifier(commnts.get(position).dp
                ,"drawable", "com.wisdom");
        c_dp.setImageResource(resID);
        return view;
    }
}
