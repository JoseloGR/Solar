package com.itesm.digital.solar.Interfaces;

import com.itesm.digital.solar.Models.Alternatives;
import com.itesm.digital.solar.Models.Coordinate;
import com.itesm.digital.solar.Models.Project;
import com.itesm.digital.solar.Models.RequestArea;
import com.itesm.digital.solar.Models.RequestBlobstore;
import com.itesm.digital.solar.Models.RequestCoordinate;
import com.itesm.digital.solar.Models.RequestCreateAlternative;
import com.itesm.digital.solar.Models.RequestCreateAlternatives;
import com.itesm.digital.solar.Models.RequestLimit;
import com.itesm.digital.solar.Models.RequestLogin;
import com.itesm.digital.solar.Models.RequestProject;
import com.itesm.digital.solar.Models.RequestRoute;
import com.itesm.digital.solar.Models.ResponseAllProjects;
import com.itesm.digital.solar.Models.ResponseArea;
import com.itesm.digital.solar.Models.ResponseBlobstore;
import com.itesm.digital.solar.Models.ResponseCoordinate;
import com.itesm.digital.solar.Models.ResponseCreateAlternatives;
import com.itesm.digital.solar.Models.ResponseDataArea;
import com.itesm.digital.solar.Models.ResponseLimit;
import com.itesm.digital.solar.Models.ResponseLogin;
import com.itesm.digital.solar.Models.ResponseProject;
import com.itesm.digital.solar.Models.ResponseRoute;
import com.itesm.digital.solar.Models.Routes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestInterface {


    @POST("Users/login")
    Call<ResponseLogin> LoginAccess(@Body RequestLogin loginRequest);
    /*
    @POST("Users/")
    Call<RegisterResponse> getRegisterUser(@Body RegisterRequest registerRequest);

    @GET("Users/{username}/")
    Call<ProfileResponse> getDataUser(@Path("username") String username);
    */

    @POST("Projects")
    Call<ResponseProject> RegisterProject(@Header("Authorization") String authToken, @Body RequestProject orderRequest);

    @POST("Coordinates")
    Call<ResponseCoordinate> RegisterCoordinate(@Header("Authorization") String authToken, @Body RequestCoordinate coordinateRequest);

    @POST("Routes")
    Call<ResponseRoute> RegisterRoute(@Header("Authorization") String authToken, @Body RequestRoute routeRequest);

    @POST("Photos")
    Call<ResponseBlobstore> RegisterPhoto(@Header("Authorization") String authToken, @Body RequestBlobstore photoRequest);

    @POST("Projects/{id}/areas")
    Call<ResponseArea> RegisterArea(@Header("Authorization") String authToken, @Body RequestArea orderArea, @Path("id") String id);

    @GET("Projects/{id}/areas")
    Call<List<ResponseDataArea>> GetAreaProject(@Header("Authorization") String authToken, @Path("id") String id);

    @POST("Areas/{id}/limits")
    Call<ResponseLimit> RegisterLimits(@Header("Authorization") String authToken, @Body RequestLimit orderLimit, @Path("id") String id);

    @GET("Projects")
    Call<List<Project>> GetAllProjects(@Header("Authorization") String authToken, @Query("userId") String userId);

    @GET("Projects/{id}")
    Call<ResponseProject> GetDataProject(@Header("Authorization") String authToken, @Path("id") String id);

    //@GET("Areas/{id}/limits")
    //Call<List<Coordinate>> GetLimits(@Header("Authorization") String authToken, @Path("id") String id);

    @GET("Areas/{id}/pointsRoute")
    Call<List<Routes>> GetRoutes(@Header("Authorization") String authToken, @Path("id") String id);

    @POST("Results/createAlternative")
    Call<ResponseCreateAlternatives> CreateAlternative(@Header("Authorization") String authToken, @Body RequestCreateAlternatives createAlternative);

    @GET("Projects/{id}/alternatives")
    Call<List<List<Alternatives>>> GetAlternatives(@Header("Authorization") String authToken, @Path("id") String id);
}
