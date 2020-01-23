package com.example.myassignment.Retrofit;

import com.example.myassignment.Model.User;
import com.example.myassignment.Model.UserStatus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MyService {

    @GET("users")
    Call<ArrayList<User>> getAllUsers();

    @GET("users/{user_id}")
    Call<User> getAUser(@Path ("user_id") int user_id);

//    @Headers("Content-Type: application/json")
    @POST("users")
    @FormUrlEncoded
    Call<String> createUser(@Field("name") String name,
                      @Field("email") String email,
                      @Field("password") String password,
                      @Field("imageURL") String imageURL);

    @PUT("users")
    @FormUrlEncoded
    Call<String> updateUser(@Field("name") String name,
                            @Field("email") String email,
                            @Field("password") String password,
                            @Field("imageURL") String imageURL);


    @GET("user_status")
    Call<ArrayList<UserStatus>> getAllUsersStatus();

    @POST("user_status")
    @FormUrlEncoded
    Call<String> addUserStatus(@Field("status_type") String statusType,
                            @Field("status_filename") String statusFileName,
                               @Field("user_id") int UserId);

    @GET("user_status/{user_id}")
    Call<ArrayList<UserStatus>> getAUserStatus(@Path ("user_id") int user_id);

    @DELETE("user_status/{status_id}")
    Call<String> deleteAStatus(@Path ("status_id") int status_id);

//    @POST("/users")
//    Call<User> createUser(@Body User user);

}
