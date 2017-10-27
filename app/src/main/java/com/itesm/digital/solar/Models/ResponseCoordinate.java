package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseCoordinate {
    public class Position {
        @SerializedName("lat")
        @Expose
        private double lat;

        @SerializedName("lng")
        @Expose
        private double lng;

        public double getLat() { return lat; }
        public double getLng() { return lng; }
    }

    @SerializedName("altitude")
    @Expose
    private double altitude;

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

    public double getAltitude() { return altitude; }
    public int getId() { return id; }
    public int getResultId() { return resultId; }
    public int getAreaId() { return areaId; }
    public Position getPosition() {
        return position;
    }
}
