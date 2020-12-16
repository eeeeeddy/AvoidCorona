package com.example.user.capstone.helper.contract;

import android.app.Activity;
import android.location.Location;
import android.text.TextWatcher;
import android.widget.AdapterView;

import com.example.user.capstone.map.Mapfragment;
import com.example.user.capstone.model.Site;
import com.example.user.capstone.model.TMapInfo;
import com.google.android.gms.common.api.GoogleApiClient;


public class SearchContract {
    public interface View {
        void setLoading(boolean loading);

        Activity getActivity();

        void setOnTouchListener(android.view.View.OnTouchListener I);

        void setMyAddressOnTouchListener(android.view.View.OnTouchListener I);

        void setFindPlace(com.google.android.libraries.places.api.model.Place place, String city);

        void setPreditInfo(TMapInfo info);

        void setTMapFail(String msg);

        void setCurrentLocation(String address, Location location);


    }

    public interface Presenter {

        void setMapFragemnt(Mapfragment fragemnt);

        void findPlace(com.google.android.libraries.places.api.model.Place place);

        void onBuildGoogleClient(GoogleApiClient googleApiClient);

        void getTMapRoute(Location startLocation, Location endLocation, Site site);

        void onLoad();

    }
}
