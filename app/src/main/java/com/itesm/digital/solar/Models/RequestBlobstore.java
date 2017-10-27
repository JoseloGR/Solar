package com.itesm.digital.solar.Models;

/**
 * Created by pabmacias28 on 27/10/17.
 */

public class RequestBlobstore {
    private String picture;
    private int id;
    private int coordinateId;

    public String getPicture() { return picture; }
    public void setPicture() { this.picture = picture; }
    public int getId() { return id; }
    public void setId() { this.id = id; }
    public int getCoordinateId() { return coordinateId; }
    public void setCoordinateId() { this.coordinateId = coordinateId; }
}
