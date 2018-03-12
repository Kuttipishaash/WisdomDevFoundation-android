package com.wisdom;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by User on 06-Mar-18.
 */

public class Locations {
    LatLng loc;
    String type,name,address;
    int num,rating,tot_comments;
    class Comments
    {
        String uid;
        String rating;
        String text;
    }
}
