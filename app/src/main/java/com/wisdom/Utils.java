package com.wisdom;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by praji on 19-Mar-18.
 */

//TODO: Move network check here

public class Utils {

    public static FeedClient getFeedClientRef() {

        //Create OkHttpClient
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        //Initialize Retrofit API Builder
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://wisdominitiatives.org")
                .addConverterFactory(GsonConverterFactory.create());

        //Build retrofit object with OkHttpClient builder
        Retrofit retrofit = builder
                .client(httpClient.build())
                .build();

        //Return a retrofit instance of FeedClient Interface
        return retrofit.create(FeedClient.class);
    }
}
