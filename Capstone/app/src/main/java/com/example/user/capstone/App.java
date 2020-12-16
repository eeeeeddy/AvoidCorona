package com.example.user.capstone;

import android.app.Application;
import android.content.Context;

import com.example.user.capstone.helper.L;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.skt.Tmap.TMapTapi;

public class App extends Application {
    private TMapTapi mTMapTapi;

    @Override
    public void onCreate() {
        super.onCreate();
        //디버깅을 할수있는 함수의 tag 를 초기화 한다.
        L.initialize("young", false);
        Places.initialize(getApplicationContext(), "AIzaSyDATAotaZR_IK9O99nQ9xq9IIdBUqPJSsc");
        PlacesClient placesClient = Places.createClient(this);
    }

    public synchronized TMapTapi getTMapTapi(Context context) {
        App app = (App) context.getApplicationContext();
        L.d("[initializeTMap]");
        if (mTMapTapi == null) {
            mTMapTapi = new TMapTapi(app);
            mTMapTapi.setSKTMapAuthentication("eeb8e205-15f3-47d3-817b-99d83af8c8ad ");
        }
        return mTMapTapi;
    }
}
