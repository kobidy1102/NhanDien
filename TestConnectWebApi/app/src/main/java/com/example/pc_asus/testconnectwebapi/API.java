package com.example.pc_asus.testconnectwebapi;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface API {
    String Base_URL="http://169.254.22.208:49428/";
    String groupID="";
    String personName="";
    @GET("api/values")
        Call<String> getResult();
    @Multipart
    @POST("api/recognition/kpop")
    Call<String> recognitionFace(@Part MultipartBody.Part photo);

    @Multipart
    @POST("api/addPersonToGroup/kpop/{personName}")
    Call<String> addFace(@Path("personName") String personName, @Part MultipartBody.Part photo);

    @POST("api/addPersonToGroup/kpop/{personName}")
    Call<String> trainingFace(@Path("personName") String personName);
}
