package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseProject {

    @SerializedName("Name")
    @Expose
    private String Name;

    @SerializedName("Address")
    @Expose
    private String Address;

    @SerializedName("Cost")
    @Expose
    private Integer Cost;

    @SerializedName("Date")
    @Expose
    private String Date;

    @SerializedName("Surface")
    @Expose
    private Integer Surface;

    @SerializedName("Id")
    @Expose
    private Integer Id;

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public Integer getCost() {
        return Cost;
    }

    public String getDate() {
        return Date;
    }

    public Integer getSurface() {
        return Surface;
    }

    public Integer getId() {
        return Id;
    }
}
