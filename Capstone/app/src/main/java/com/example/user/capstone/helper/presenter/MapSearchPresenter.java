package com.example.user.capstone.helper.presenter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.VolleyError;
import com.example.user.capstone.helper.L;
import com.example.user.capstone.helper.contract.SearchContract;
import com.example.user.capstone.helper.volley.LocationProvider;
import com.example.user.capstone.helper.volley.VolleyResult;
import com.example.user.capstone.helper.volley.VolleyService;
import com.example.user.capstone.map.Mapfragment;
import com.example.user.capstone.model.Site;
import com.example.user.capstone.model.TMapInfo;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.user.capstone.MatchingMapActivity.AUTOCOMPLETE_REQUEST_CODE;

public class MapSearchPresenter implements SearchContract.Presenter {

    private Context mContext;
    private SearchContract.View mView;

    //    private AutoCompleteAdapter mAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private AlertDialog dialog;
    private Mapfragment mMapfragment;


    private final long DEFAULT_WAIT_MS = 33L;

    public MapSearchPresenter(Context mContext, SearchContract.View mView) {
        this.mContext = mContext;
        this.mView = mView;

    }

    @Override
    public void setMapFragemnt(Mapfragment fragemnt) {
        this.mMapfragment = fragemnt;
    }


    @Override
    public void findPlace(com.google.android.libraries.places.api.model.Place place) {
        L.e("");
        if (place == null) {
            return;
        }
        if (place.getLatLng() == null) {
            return;
        }
        L.d("주소 : " + place.getName() + " 로케이션 정보 : " + place.getLatLng() + " 위치정보 : " + place.getLatLng());
        mView.setFindPlace(place, place.getName());
    }

    @Override
    public void onBuildGoogleClient(GoogleApiClient googleApiClient) {
        this.mGoogleApiClient = googleApiClient;
    }

    @Override
    public void getTMapRoute(Location startLocation, Location endLocation, Site site) {
        L.d("getTMapRoute init " + startLocation + "/" + endLocation);
        mView.setLoading(true);//로딩을 활성화 시킨다.


        //추천경로를 불러오는 서버로직
        VolleyService volleyService = new VolleyService(new VolleyResult() {
            @Override
            public void notifySuccess(String type, JSONObject response) {
                L.e("[Volley notifySuccess] " + response);
                try {
                    ArrayList<LatLng> list = new ArrayList<>();
                    TMapInfo tMapInfo = new TMapInfo();
                    JSONArray ja = response.getJSONArray("features");
                    if (ja.length() > 0) {
                        L.i(":::ja length " + ja.length());
                        for (int i = 0; i < ja.length(); i++) {
                            L.i(":::: i  " + i);
                            JSONObject features = ja.getJSONObject(i);
                            JSONObject geometry = features.getJSONObject("geometry");
                            JSONObject properties = features.getJSONObject("properties");

                            //추천 요금 거리 등을 불러온후.
                            if (properties.has("totalDistance")) {
                                String totalDistance = properties.getString("totalDistance");
                                L.i("totalDistance " + totalDistance);
                                tMapInfo.setTotalDistance(totalDistance);
                            }

                            if (properties.has("totalTime")) {
                                String totalTime = properties.getString("totalTime");
                                L.i("totalTime " + totalTime);
                                tMapInfo.setTotalTime(totalTime);
                            }

                            if (properties.has("totalFare")) {
                                String totalFare = properties.getString("totalFare");
                                L.i("totalFare " + totalFare);
                                tMapInfo.setTotalFare(totalFare);
                            }

                            if (properties.has("taxiFare")) {
                                String taxiFare = properties.getString("taxiFare");
                                L.i("taxiFare " + taxiFare);
                                tMapInfo.setTaxiFare(taxiFare);
                            }
                            String geometryType = "";
                            L.e(":::geometry.has(type) : " + geometry.has("type"));
                            if (geometry.has("type")) {
                                geometryType = geometry.getString("type");
                                L.i("geometryType " + geometryType);
                            }
                            L.e(":::geometry.has(coordinates) : " + geometry.has("coordinates"));
                            JSONArray coordinates = geometry.getJSONArray("coordinates");
                            L.i(":::coordinates : " + coordinates.length());
                            //추천경로가 있다면? 추천경로를 설정해준다.
                            if (!geometryType.equalsIgnoreCase("")) {
                                if (geometryType.equalsIgnoreCase("Point")) {
                                    L.i("[geometryType Point]");
                                    String array[] = new String[2];
                                    for (int size = 0; size < coordinates.length(); size++) {
                                        L.i("::::coordinates info : " + coordinates.getString(size));
                                        array[size] = coordinates.getString(size);
                                    }
                                    list.add(new LatLng(Double.valueOf(array[1]), Double.valueOf(array[0])));
                                } else {
                                    L.i("[geometryType LineString]");
                                    for (int size = 0; size < coordinates.length(); size++) {
                                        L.i("::::coordinates info : " + coordinates.getString(size));
                                        String[] split = coordinates.getString(size).split(",");
                                        list.add(new LatLng(Double.valueOf(getStringNumberFormat(split[1])), Double.valueOf(getStringNumberFormat(split[0]))));

                                    }
                                }
                            }

                        }


                        HashMap<String, Site.Data> patientMap = new HashMap<>();

                        if (site != null) {
                            L.e(":::최단경로 검색 시작...");
                            for (LatLng item : list) {
                                for (Site.Data siteItem : site.records) {
                                    if (!siteItem.getLongitude().equalsIgnoreCase("") && !siteItem.getLatitude().equalsIgnoreCase("")) {
                                        double distance = getDistance(
                                                Double.parseDouble(siteItem.getLatitude()),
                                                Double.parseDouble(siteItem.getLongitude()),
                                                item.latitude,
                                                item.longitude);
                                        if (distance < 100) {
                                            patientMap.put(siteItem.getPlaceName(), siteItem);
                                        }
                                    }
                                }
                            }
                        }


                        //추천경로 탐색 끝
                        //end
                        mView.setPreditInfo(tMapInfo);


                        L.i(":::::mMapfragment : " + mMapfragment);
                        L.i(":::::list : " + list.size());

                        //로딩창을 닫고 추천경로를 구글지도에 그려주도록한다.
                        mView.setLoading(false);
                        mMapfragment.drawBoundaryOnMap(list, patientMap, tMapInfo.getTotalDistance(), time -> {
                            long delayTime = DEFAULT_WAIT_MS + time;
                            new android.os.Handler().postDelayed(() -> L.i("[Map Load OnResultBoundraryListener complte]"), delayTime);
                        });


                    } else {
                        mView.setTMapFail("정보를 불러오는데 실패 하였습니다.");
                    }
                } catch (JSONException e) {
                    L.i("e : " + e.getMessage());
                }


            }

            @Override
            public void notifyError(VolleyError error) {
                L.e("[Volley Error] " + error.getMessage());
            }
        }, mContext);
        volleyService.getTMapRoutes(startLocation, endLocation);
    }

    @Override
    public void onLoad() {
        L.i("[onLoad init]");
        mView.setOnTouchListener(mOnTouchListener);
        mView.setMyAddressOnTouchListener(mOnMyAddresTouchListener);
    }

    private View.OnTouchListener mOnMyAddresTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                if (!checkGPS(mContext)) {
                    showPopup();
                    return true;
                } else {
                    //현재 사용자의 위치를 불러오는 로직이다.
                    new LocationProvider().getLocation(mView.getActivity(), new LocationProvider.LocationResultCallback() {
                        @Override
                        public void gotLocation(Location location) {
                            L.e(":::::::::::gettting current location  :  " + location);
                            if (location != null) {
                                VolleyService volleyService = new VolleyService(new VolleyResult() {
                                    @Override
                                    public void notifySuccess(String type, JSONObject response) {
                                        try {
                                            JSONArray ja = response.getJSONArray("documents");
                                            if (ja.length() > 0) {
                                                String address_name = "";
                                                for (int i = 0; i < ja.length(); i++) {
                                                    JSONObject wtmJson = ja.getJSONObject(i);
                                                    if (wtmJson.has("address")) {
                                                        JSONObject address = wtmJson.getJSONObject("address");
                                                        address_name = address.getString("address_name");
                                                        L.e(":::address_name : " + address_name);

                                                    }
                                                }

                                                if (!address_name.equalsIgnoreCase("")) {
                                                    mView.setCurrentLocation(address_name, location);
                                                }

                                            }
                                        } catch (JSONException e) {

                                        }
                                    }

                                    @Override
                                    public void notifyError(VolleyError error) {

                                    }
                                }, mView.getActivity());
                                volleyService.getGeoWTM(location);
                            }
                        }
                    });
                    return false;
                }

            }
            return false;
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                if (!checkGPS(mContext)) {
                    showPopup();
                    return true;
                } else {
                    List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(mContext);
                    mView.getActivity().startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                    return false;
                }

            }
            return false;
        }
    };

    public boolean checkGPS(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPS) {

            return true;
        } else {

        }
        return false;
    }

    private void showPopup() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mView.getActivity());
        builder.setMessage("GPS 장치를 켜주세요.");
        builder.setCancelable(false);
        builder.setNegativeButton("확인", (dialog1, which) -> {
            dialog.dismiss();

        });

        dialog = builder.create();
        dialog.show();
    }


    public static String getStringNumberFormat(String str) {
        if (str.contains("]")) {
            str = str.replace("]", "");
        }

        if (str.contains("[")) {
            str = str.replace("[", "");
        }
        return str;
    }

    private Double getDistance(
            Double lat1,
            Double lon1,
            Double lat2,
            Double lon2
    ) {
        Double theta = lon1 - lon2;
        Double dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta)));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist *= 60.0 * 1.1515;
        dist *= 1609.344;
        return dist;

    }


    private Double deg2rad(Double deg) {
        return (deg * Math.PI / 180.0);
    }

    private Double rad2deg(Double rad) {
        return (rad * 180 / Math.PI);
    }

}
