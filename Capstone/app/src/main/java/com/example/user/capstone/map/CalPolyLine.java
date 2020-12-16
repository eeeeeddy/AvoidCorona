package com.example.user.capstone.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class CalPolyLine {
    private LatLngBounds latLngBounds;
    private List<LatLng> reducePointList;

    public CalPolyLine() {
    }

    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }

    public void setLatLngBounds(LatLngBounds latLngBounds) {
        this.latLngBounds = latLngBounds;
    }

    public List<LatLng> getReducePointList() {
        return reducePointList;
    }

    public void setReducePointList(List<LatLng> reducePointList) {
        this.reducePointList = reducePointList;
    }
}
