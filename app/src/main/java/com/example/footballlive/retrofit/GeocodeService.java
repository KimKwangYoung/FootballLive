package com.example.footballlive.retrofit;

import com.example.footballlive.data.AddressData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodeService {
    @GET("geocode")
    Call<AddressData> getGeocoding(
            @Query("query") String query,
            @Query("X-NCP-APIGW-API-KEY-ID") String clientKey,
            @Query("X-NCP-APIGW-API-KEY") String secretKey);
}
