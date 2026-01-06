package com.example.pocketmanager;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface OCRApiService {

    @Multipart
    @POST("parse/image")
    Call<ResponseBody> getOCRResult(
            @Part MultipartBody.Part file,
            @Query("apikey") String apiKey
    );
}
