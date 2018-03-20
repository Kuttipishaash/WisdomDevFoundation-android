package com.wisdom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by praji on 04-Mar-18.
 */

public interface FeedClient {

    //Acquire newsfeed list
    @GET("/wp-json/wp/v2/posts")
    Call<List<FeedItem>> feeds();

    //Acquire images
    @GET("/wp-json/wp/v2/media/{image}")
    Call<FeedImage> image(@Path("image") String imageId);
}
