package com.itesm.digital.solar.Models;

public class RequestLimit {

    private Position position;
    private String altitude;
    private String id;
    private String resultId;
    private String areaId;

    public String getAltitude(){ return altitude; }

    public void setAltitude(String _altitude){ altitude = _altitude; }

    public String getResultId(){ return resultId; }

    public void setResultId(String _resultId){ resultId = _resultId;}

    public String getAreaId(String _areaId){ return areaId; }

    public void setAreaId(String _areaId){ areaId = _areaId; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
