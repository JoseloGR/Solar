package com.itesm.digital.solar.Interfaces;

import com.itesm.digital.solar.Models.RequestLogin;
import com.itesm.digital.solar.Models.RequestProject;
import com.itesm.digital.solar.Models.ResponseLogin;
import com.itesm.digital.solar.Models.ResponseProject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RequestInterface {


    @POST("Users/login")
    Call<ResponseLogin> LoginAccess(@Body RequestLogin loginRequest);
    /*
    @POST("Users/")
    Call<RegisterResponse> getRegisterUser(@Body RegisterRequest registerRequest);

    @GET("Users/{username}/")
    Call<ProfileResponse> getDataUser(@Path("username") String username);
    */

    @POST("Projects/")
    Call<ResponseProject> RegisterProject(@Body RequestProject orderRequest);

}
