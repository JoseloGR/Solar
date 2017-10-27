package com.itesm.digital.solar.Models;

public class RequestLimit {

    private String lat;
    private String lng;
    private String altitude;
    private String resultId;
    private String areaId;

    public String getLat(){ return lat; }

    public void setLat(String _lat){ lat = _lat; }

    public String getLng(){ return lng; }

    public void setLng(String _lng){ lng = _lng; }

    public String getAltitude(){ return altitude; }

    public void setAltitude(String _altitude){ altitude = _altitude; }

    public String getResultId(){ return resultId; }

    public void setResultId(String _resultId){ resultId = _resultId;}

    public String getAreaId(String _areaId){ return areaId; }

    public void setAreaId(String _areaId){ areaId = _areaId; }
}
