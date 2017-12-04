package com.itesm.digital.solar.Models;

/**
 * Created by pabmacias28 on 27/10/17.
 */

public class RequestBlobstore {
    private String image;
    private String id;
    private String coordinateId;

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCoordinateId() { return coordinateId; }
    public void setCoordinateId(String coordinateId) { this.coordinateId = coordinateId; }
}
