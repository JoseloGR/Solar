package com.itesm.digital.solar.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Alternatives {

    @SerializedName("position")
    @Expose
    private Position position;

    @SerializedName("direction")
    @Expose
    private String direction;

    @SerializedName("angle")
    @Expose
    private String angle;

    @SerializedName("generatedEnergy")
    @Expose
    private String generatedEnergy;

    @SerializedName("roi")
    @Expose
    private String roi;

    @SerializedName("payback")
    @Expose
    private String payback;

    @SerializedName("costoInstalacion")
    @Expose
    private String costoInstalacion;

    @SerializedName("ganancias")
    @Expose
    private String ganancias;

    @SerializedName("idPanel")
    @Expose
    private String idPanel;

    @SerializedName("idInverter")
    @Expose
    private String idInverter;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("areaId")
    @Expose
    private String areaId;

    @SerializedName("savings")
    @Expose
    private List<String> savings = null;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public String getGeneratedEnergy() {
        return generatedEnergy;
    }

    public void setGeneratedEnergy(String generatedEnergy) {
        this.generatedEnergy = generatedEnergy;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }

    public String getPayback() {
        return payback;
    }

    public void setPayback(String payback) {
        this.payback = payback;
    }

    public String getCostoInstalacion() {
        return costoInstalacion;
    }

    public void setCostoInstalacion(String costoInstalacion) {
        this.costoInstalacion = costoInstalacion;
    }

    public String getGanancias() {
        return ganancias;
    }

    public void setGanancias(String ganancias) {
        this.ganancias = ganancias;
    }

    public String getIdPanel() {
        return idPanel;
    }

    public void setIdPanel(String idPanel) {
        this.idPanel = idPanel;
    }

    public String getIdInverter() {
        return idInverter;
    }

    public void setIdInverter(String idInverter) {
        this.idInverter = idInverter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public List<String> getSavings() {
        return savings;
    }

    public void setSavings(List<String> savings) {
        this.savings = savings;
    }
}
