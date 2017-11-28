package com.itesm.digital.solar.Models;

public class SolarProject {

    private String name;
    private String address;
    private String cost;
    private String date;
    private String surface;
    private String id;
    private String userId;

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
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
