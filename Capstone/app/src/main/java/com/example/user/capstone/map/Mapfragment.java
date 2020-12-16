package com.example.user.capstone.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.capstone.R;
import com.example.user.capstone.helper.L;
import com.example.user.capstone.helper.dialog.WarningDialog;
import com.example.user.capstone.model.Site;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Administrator on 2017-05-17.
 */
public class Mapfragment extends Fragment implements OnMapReadyCallback {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    private final double CAL_TOLERANCE = 3L;
    private GoogleMap mGoogleMap;
    private View mView;
    private View mMarkerView;
    private SupportMapFragment mMapFragment;
    private static Mapfragment mInstance;
    private UiSettings mUiSettings;
    private Handler mHandler = new Handler();
    private CameraMoveCompleteListener mCameraMoveCompleteListener;
    private float mPolyLineWidth;
    private int mBoundaryPadding;
    private WarningDialog warningDialog;
    private Marker mGoalMaker;

    public interface CameraMoveCompleteListener {
        void animateCameraComplete(Location location);
    }

    public void setCameraMoveCompleteListener(CameraMoveCompleteListener listener) {
        this.mCameraMoveCompleteListener = listener;
    }


    public static Mapfragment getInstance() {
        if (mInstance == null) {
            mInstance = new Mapfragment();
        }
        return mInstance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.map_fragment, container, false);

        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = childFragMan.beginTransaction();
        mMapFragment = SupportMapFragment.newInstance();
        mMapFragment.getMapAsync(this);
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
        mPolyLineWidth = TypedValue.applyDimension(1, 4.0F, getContext().getResources().getDisplayMetrics());
        mBoundaryPadding = getResources().getDimensionPixelSize(R.dimen.exercise_map_boundary_padding);
        int width = getResources().getDisplayMetrics().widthPixels;
        mBoundaryPadding = (int) (width * 0.12);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        warningDialog = WarningDialog.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View getView() {
        return mView;
    }

    private void loadMarkerIcon(final Marker marker, int rsid) {

//        Glide.with(this).load(rsid)
//                .asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
//                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
//                marker.setIcon(icon);
//            }
//        });
    }


    public void updateCamera(final com.google.android.libraries.places.api.model.Place place, final String city) {
        //검색해서 불러온 동네의 place 값 (좌표 ,주소가 포함되어있는 객체)를 불러와서 db에 있는 데이터들과 500m 이하 체크를 하도록한다.
        if (place != null && !TextUtils.isEmpty(city)) {
            final long Time = System.currentTimeMillis();
            final LatLng latLng = place.getLatLng();
            Location goalLocation = new Location("goal_location");
            goalLocation.setLatitude(latLng.latitude);
            goalLocation.setLongitude(latLng.longitude);

            if (mGoalMaker != null) {
                mGoalMaker.remove();
            }
//            mGoogleMap.clear();
            mGoalMaker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));
            loadMarkerIcon(mGoalMaker, R.drawable.end_marker);

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mHandler.postDelayed(() -> mCameraMoveCompleteListener.animateCameraComplete(goalLocation), (System.currentTimeMillis() - Time));
                }

                @Override
                public void onCancel() {
                }
            });
        } else {
            Toast.makeText(getActivity(), "해당되는 주소 정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private Site mSite;
    private ArrayList<Polyline> polyLineList = new ArrayList<>();


    @Override
    public void onMapReady(GoogleMap googleMap) {
        L.e("onMapReady");
        L.d("googleMap : " + googleMap);
        if (googleMap == null) return;

        mGoogleMap = googleMap;
        mMarkerView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_marker_layout, null);
        mUiSettings = mGoogleMap.getUiSettings();
        mUiSettings.setMyLocationButtonEnabled(true);
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            if (getLocation() == null) return;
            drawBoundaryOnMap(null, null, null, null);
            addMarkers(mSite);
        }

    }

    public void setSite(Site site) {
        this.mSite = site;
    }

    private void addMarkers(Site list) {
        if (list == null) return;
        list.records.forEach(data -> {
            if (!data.getLongitude().equalsIgnoreCase("") && !data.getLatitude().equalsIgnoreCase("")) {
                MarkerOptions makerOptions = new MarkerOptions();
                makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
                        .position(new LatLng(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude())))
                        .title(data.getPlaceName()); // 타이틀.
                mGoogleMap.addMarker(makerOptions);
            }
        });

    }

    public void drawBoundaryOnMap(ArrayList<LatLng> polyLine, HashMap<String, Site.Data> patientMap, String totalDiatance, OnResultBoundraryListener listener) {
        final long time = System.currentTimeMillis();
//        clearLine();
        GetMapDataTask getMapDataTask = new GetMapDataTask.Builder().setFetcher(() -> getDataSet(polyLine)).setCallback(data -> {
            L.i("[GetMapDataTask callback] : " + data);
            if (data != null) {
                CalPolyLine calPolyLine = (CalPolyLine) data;


                if (polyLineList.size() > 0) {
                    //기존에 폴리라인이 있으면 한번제거 해준다.
                    Polyline polyline = polyLineList.get(polyLineList.size() - 1);
                    polyline.remove();
                    polyLineList.clear();
                }

                polyLineList.add(mGoogleMap.addPolyline(new PolylineOptions().addAll(calPolyLine.getReducePointList()).visible(true).geodesic(true).color(Color.parseColor("#e02e6a")).
                        width(mPolyLineWidth).clickable(false)));


                if (patientMap != null && patientMap.size() > 0) {
                    warningDialog.setDialogText(convertMeterToKilometer(Integer.parseInt(totalDiatance)) + "km", String.valueOf(patientMap.size()));
                    warningDialog.show(getChildFragmentManager(), warningDialog.getTag());
                } else {
                    warningDialog.setDialogText(convertMeterToKilometer(Integer.parseInt(totalDiatance)) + "km", "0");
                    warningDialog.show(getChildFragmentManager(), warningDialog.getTag());
                }

                updateLocation(calPolyLine.getLatLngBounds());
            } else {
                updateLocation(getLocation(), true);
            }

            if (listener != null)
                listener.onCompleteBoundrary(System.currentTimeMillis() - time);
        }).build();
        getMapDataTask.execute();
    }

    private CalPolyLine getDataSet(ArrayList<LatLng> polyLineList) {
        CalPolyLine calPolyLine = null;
        ArrayList<LatLng> list = polyLineList;
        if (list != null && list.size() > 0) {
            calPolyLine = new CalPolyLine();
            List<LatLng> reducePointList = PolyUtil.douglasPeuckerReduction(list, 3L);
            Iterator iterator = reducePointList.iterator();
            final LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
            while (iterator.hasNext()) {
                LatLng latLng = (LatLng) iterator.next();
                latLngBounds.include(latLng);
            }
            calPolyLine.setReducePointList(reducePointList);
            calPolyLine.setLatLngBounds(latLngBounds.build());

        }
        return calPolyLine;
    }


    public void clearLine() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
    }


    Projection projection;
    Paint paint;

    Location loc = null;

    public Location getLocation() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocationGPS != null) {
                    return lastKnownLocationGPS;
                } else {
                    Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    return loc;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    public void onLoation() {
        if (mGoogleMap != null) {
            onMapReady(mGoogleMap);
        }
    }

    private void updateLocation(LatLngBounds bounds) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, mBoundaryPadding));
    }

    private void updateLocation(List<LatLng> gpslist) {
        Location updateLocation = new Location(Mapfragment.class.getSimpleName());
        L.e("gpslist : " + gpslist.size());
        for (int i = 0; i < gpslist.size(); i++) {
            updateLocation.setLatitude(gpslist.get(i).latitude);
            updateLocation.setLongitude(gpslist.get(i).longitude);
        }
        CameraPosition position = new CameraPosition.Builder().target(new LatLng(updateLocation.getLatitude(), updateLocation.getLongitude()))
                .zoom(16f)
                .bearing(0)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    private void updateLocation(Location location, boolean flag) {
        if (location == null) return;

        if (flag) {
            //CameraPosition 유연성 확보를 위해 카메라를 주어진 위치로 이동하는 애니메이션을 보여준다.
            CameraPosition position = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(16f)
                    .bearing(0)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), Math.max(30, 1), null);
        } else {
            //카메라의 위도를 변경할때사용
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }

    public String convertMeterToKilometer(int totalDistance) {
        double ff = totalDistance / 1000.0;
        BigDecimal bd = BigDecimal.valueOf(ff);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return String.valueOf(bd.doubleValue());
    }


}
