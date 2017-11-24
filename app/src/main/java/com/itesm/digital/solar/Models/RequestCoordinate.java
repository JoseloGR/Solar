package com.itesm.digital.solar.Models;

public class RequestCoordinate {
    /*public static class Position {
        private double lat;
        private double lng;

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = this.lat; }
        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
        public double getAltitude() { return lat; }
    }*/

    private double altitude;
    private String id;
    private String resultId;
    private String areaId;
    private Position position;

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
