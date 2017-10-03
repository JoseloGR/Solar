package com.itesm.digital.solar.Models;

public class RequestProject {

    private String Name;
    private String Address;
    private String Cost;
    private String Date;
    private String Surface;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSurface() {
        return Surface;
    }

    public void setSurface(String surface) {
        Surface = surface;
    }
}
