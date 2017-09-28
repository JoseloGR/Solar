package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseProject {

    @SerializedName("statusCode")
    @Expose
    private String statusCode;

    @SerializedName("message")
    @Expose
    private String message;

    public String getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
