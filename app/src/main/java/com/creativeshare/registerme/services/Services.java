package com.creativeshare.registerme.services;


import com.creativeshare.registerme.models.AppDataModel;
import com.creativeshare.registerme.models.BankDataModel;
import com.creativeshare.registerme.models.Slider_Model;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Services {
    @FormUrlEncoded
    @POST("api/contact_us")
    Call<ResponseBody> contact_us(@Field("name") String name,
                                  @Field("email") String email,
                                  @Field("message") String message,
                                  @Field("subject") String subject

    );
    @GET("api/all_slider")
    Call<Slider_Model> get_slider();

    @GET("api/aboutUs")
    Call<AppDataModel> getabout(
            );

    @GET("api/condtions")
    Call<AppDataModel> getterms(
            );

    @GET("api/all_banks")
    Call<BankDataModel> getBankAccount();
}
