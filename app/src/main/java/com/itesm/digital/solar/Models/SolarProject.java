package com.itesm.digital.solar.Models;

public class SolarProject {

    private String Name;
    private String Address;
    private String Cost;
    private String Date;
    private String Surface;
    private String id;
    private String userId;

    /*
    public SolarProject(String name,
                        String address,
                        String cost,
                        String date,
                        String surface,
                        String id,
                        String userId){
        this.name = name;
        this.address = address;
        this.cost = cost;
        this.date = date;
        this.surface = surface;
        this.id = id;
        this.userId = userId;
    }*/

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public String getCost() {
        return Cost;
    }

    public String getDate() {
        return Date;
    }

    public String getSurface() {
        return Surface;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }
}
