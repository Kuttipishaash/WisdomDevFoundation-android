package com.wisdom;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by User on 06-Mar-18.
 */

public class Locations {
    LatLng loc;
    String type,name,address;
    int num,rating,tot_comments;
    static class Comments
    {
      //  String uid;
        String name;
        String dp;
        String rating;
        String text;
    }
}
