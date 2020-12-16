package com.example.user.capstone.model;

import com.example.user.capstone.helper.L;

public class TMapLocation {
    private String TLatitude;
    private String TLongitude;

    public TMapLocation(String TLatitude, String TLongitude) {
        L.i(":::::TMapLocation TLatitude : " + TLatitude + " TLongitude : " + TLongitude);
        this.TLatitude = TLatitude;
        this.TLongitude = TLongitude;
    }

    public String getTLatitude() {
        return TLatitude;
    }

    public void setTLatitude(String TLatitude) {
        this.TLatitude = TLatitude;
    }

    public String getTLongitude() {
        return TLongitude;
    }

    public void setTLongitude(String TLongitude) {
        this.TLongitude = TLongitude;
    }

    @Override
    public String toString() {
        return "TMapLocation{" +
                "TLatitude='" + TLatitude + '\'' +
                ", TLongitude='" + TLongitude + '\'' +
                '}';
    }
}
