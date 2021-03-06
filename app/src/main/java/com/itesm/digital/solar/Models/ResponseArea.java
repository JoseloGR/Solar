package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseArea {

    @SerializedName("solarRadiation")
    @Expose
    private Double solarRadiation;

    @SerializedName("azimuth")
    @Expose
    private Double azimuth;

    @SerializedName("surface")
    @Expose
    private Double surface;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("projectId")
    @Expose
    private Integer projectId;

    @SerializedName("center")
    @Expose
    private Center center;

    public Center getCenter(){ return center; }

    public Double getSolarRadiation() {
        return solarRadiation;
    }

    public Double getAzimuth() {
        return azimuth;
    }

    public Double getSurface() {
        return surface;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProjectId() {
        return projectId;
    }
}
