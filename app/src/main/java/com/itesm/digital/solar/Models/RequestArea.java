package com.itesm.digital.solar.Models;

public class RequestArea {

    private String solarRadiation;
    private String azimuth;
    private String surface;
    private String id;
    private String projectId;
    private Center center;

    public String getSolarRadiation(){ return solarRadiation; }

    public void setSolarRadiation(String _solarRadiation){ solarRadiation = _solarRadiation; }

    public String getAzimuth(){ return azimuth; }

    public void setAzimuth(String _azimuth){ azimuth = _azimuth; }

    public String getSurface(){ return surface; }

    public void setSurface(String _surface){ surface = _surface; }

    public String getProjectId() { return projectId; }

    public void setProjectId(String _projectId ) { projectId = _projectId; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }
}
