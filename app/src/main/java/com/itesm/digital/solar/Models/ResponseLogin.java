package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseLogin {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("ttl")
    @Expose
    private String ttl;

    @SerializedName("created")
    @Expose
    private String created;

    @SerializedName("userId")
    @Expose
    private String userId;

    public String getId() {
        return id;
    }

    public String getTtl() {
        return ttl;
    }

    public String getCreated() {
        return created;
    }

    public String getUserId() {
        return userId;
    }
}
