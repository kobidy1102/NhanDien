package com.example.pc_asus.testconnectwebapi;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface API {
    String Base_URL="http://169.254.22.208:49428/";
    @GET("api/values")
        Call<String> getResult();
    @Multipart
    @POST("api/upload/kpop")
    Call<String> upLoadPhoto(@Part MultipartBody.Part photo);



}
