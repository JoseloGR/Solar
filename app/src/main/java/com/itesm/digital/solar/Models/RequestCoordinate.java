package com.itesm.digital.solar.Models;

public class RequestCoordinate {
    public class Position {
        private double lat;
        private double lng;

        public double getLat() { return lat; }
        public void setLat() { this.lat = lat; }
        public double getLng() { return lng; }
        public void setLng() { this.lng = lng; }
        public double getAltitude() { return lat; }
    }

    private double altitude;
    private int id;
    private int resultId;
    private int areaId;
    private Position position;

    public void setAltitude() { this.altitude = altitude; }
    public int getId() { return id; }
    public void setId() { this.id = id; }
    public int getResultId() { return resultId; }
    public void setResultId() { this.resultId = resultId; }
    public int getAreaId() { return areaId; }
    public void setAreaId() { this.areaId = areaId; }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
