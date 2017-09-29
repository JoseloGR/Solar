package com.itesm.digital.solar.Models;

public class RequestProject {

    private String Name;
    private String Address;
    private Integer Cost;
    private String Date;
    private Integer Surface;

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

    public Integer getCost() {
        return Cost;
    }

    public void setCost(Integer cost) {
        Cost = cost;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Integer getSurface() {
        return Surface;
    }

    public void setSurface(Integer surface) {
        Surface = surface;
    }
}
