package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Routes {
    @SerializedName("altitude")
    @Expose
    private double altitude;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("resultId")
    @Expose
    private String resultId;

    @SerializedName("areaId")
    @Expose
    private String areaId;

    @SerializedName("position")
    @Expose
    private Position position;

    public double getAltitude() { return altitude; }
    public void setAltitude(double altitude) { this.altitude = altitude; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }
    public String getAreaId() { return areaId; }
    public void setAreaId(String areaId) { this.areaId = areaId; }
    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) { this.position = position; }
}