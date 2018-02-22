package com.wisdom;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by User on 22-Feb-18.
 */

public class Institution {
    String num;
    LatLng loc;
    String address,rating,type;
    static class CommentsRating
    {
        String name,comment,rating,dp;
    }
    ArrayList<CommentsRating> cmmnts= new ArrayList<CommentsRating>();

}
