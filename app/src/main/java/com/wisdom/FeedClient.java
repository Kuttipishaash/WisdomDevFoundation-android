package com.wisdom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by praji on 04-Mar-18.
 */

public interface FeedClient {
    @GET("/wp-json/wp/v2/posts")
    Call<List<FeedItem>> feeds();
}
