package com.itesm.digital.solar.Models;

public class RequestRoute {

    private double altitude;
    private String id;
    private String resultId;
    private String areaId;
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
    public void setPosition(Position position) {
        this.position = position;
    }
}
