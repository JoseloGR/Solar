package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pabmacias28 on 27/10/17.
 */

public class ResponseBlobstore {
    @SerializedName("picture")
    @Expose
    private String picture;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("coordinateId")
    @Expose
    private Integer coordinateId;

    public String getPicture() { return picture; }
    public int getId() { return id; }
    public int getCoordinateId() { return coordinateId; }
}
