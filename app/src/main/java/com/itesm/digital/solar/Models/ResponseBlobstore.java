package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ResponseBlobstore {
    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("coordinateId")
    @Expose
    private String coordinateId;

    public String getImage() { return image; }
    public String getId() { return id; }
    public String getCoordinateId() { return coordinateId; }
}
