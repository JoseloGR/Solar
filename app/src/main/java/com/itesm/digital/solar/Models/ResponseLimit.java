package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseLimit {

    @SerializedName("lat")
    @Expose
    private Double lat;

    @SerializedName("lng")
    @Expose
    private Double lng;

    @SerializedName("altitude")
    @Expose
    private Double altitude;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("resultId")
    @Expose
    private Integer resultId;

    @SerializedName("areaId")
    @Expose
    private Integer areaId;

    public Double getLat(){ return lat; }

    public Double getLng(){ return lng; }

    public Double getAltitude(){ return altitude; }

    public Integer getId(){ return id; }

    public Integer getResultId(){ return resultId; }

    public Integer getAreaId() { return areaId; }
}
