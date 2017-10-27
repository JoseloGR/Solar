package com.itesm.digital.solar.Models;

public class RequestArea {

    private String lat;
    private String lng;
    private String solarRadiation;
    private String azimuth;
    private String surface;
    private String projectId;

    public String getLatitude(){ return lat; }

    public void setLatitude(String latitude){ lat = latitude; }

    public String getLongitude(){ return lng; }

    public void setLongitude(String longitude){ lng = longitude; }

    public String getSolarRadiation(){ return solarRadiation; }

    public void setSolarRadiation(String _solarRadiation){ solarRadiation = _solarRadiation; }

    public String getAzimuth(){ return azimuth; }

    public void setAzimuth(String _azimuth){ azimuth = _azimuth; }

    public String getSurface(){ return surface; }

    public void setSurface(String _surface){ surface = _surface; }

    public String getProjectId() { return projectId; }

    public void setProjectId(String _projectId ) { projectId = _projectId; }
}
