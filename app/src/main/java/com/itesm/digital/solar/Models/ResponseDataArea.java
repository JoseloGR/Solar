package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseDataArea {

    @SerializedName("solarRadiation")
    @Expose
    private String solarRadiation;

    @SerializedName("azimuth")
    @Expose
    private String azimuth;

    @SerializedName("surface")
    @Expose
    private String surface;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("vuelo")
    @Expose
    private Boolean vuelo;

    @SerializedName("projectId")
    @Expose
    private Integer projectId;

    @SerializedName("center")
    @Expose
    private Center center;

    public String getSolarRadiation() {
        return solarRadiation;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public String getSurface() {
        return surface;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getVuelo() {
        return vuelo;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Center getCenter() {
        return center;
    }
}
