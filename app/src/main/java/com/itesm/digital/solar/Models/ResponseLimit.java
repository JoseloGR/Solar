package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseLimit {

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

    @SerializedName("position")
    @Expose
    private Position position;

    public Position getPosition() { return position; }

    public Double getAltitude(){ return altitude; }

    public Integer getId(){ return id; }

    public Integer getResultId(){ return resultId; }

    public Integer getAreaId() { return areaId; }
}
