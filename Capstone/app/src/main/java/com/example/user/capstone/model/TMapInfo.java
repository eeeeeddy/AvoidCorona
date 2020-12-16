package com.example.user.capstone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TMapInfo implements Serializable{
    private String totalDistance;
    private String totalTime;
    private String totalFare;
    private String taxiFare;

    public TMapInfo() {
    }


    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getTaxiFare() {
        return taxiFare;
    }

    public void setTaxiFare(String taxiFare) {
        this.taxiFare = taxiFare;
    }

    @Override
    public String toString() {
        return "TMapInfo{" +
                "totalDistance='" + totalDistance + '\'' +
                ", totalTime='" + totalTime + '\'' +
                ", totalFare='" + totalFare + '\'' +
                ", taxiFare='" + taxiFare + '\'' +
                '}';
    }
}
