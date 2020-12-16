package com.example.user.capstone.helper.volley;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.example.user.capstone.helper.L;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class VolleyService {

    private VolleyResult resultCallback = null;
    private Context mContext = null;


    private Response.Listener<JSONObject> successListener = response -> resultCallback.notifySuccess(null, response);

    private Response.ErrorListener errorListener = error -> resultCallback.notifyError(error);

    public VolleyService(VolleyResult resultCallback, Context context) {
        this.resultCallback = resultCallback;
        mContext = context;
    }

    public void getTMapRoutes(Location startLocation, Location endLocation) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        String url = "https://apis.openapi.sk.com/tmap/routes?version=1&format=json";
//        String url = "https://apis.openapi.sk.com/tmap/routes?version=1&format=json";
        L.i("[VolleyService getComPareFriend] " + url);

        Request<JSONObject> req = new Request<JSONObject>(Request.Method.POST, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.e("[onErrorResponse] " + error);
                L.e("[onErrorResponse] " + error.getStackTrace());
                try {
                    byte[] htmlBodyBytes = error.networkResponse.data;
                    Log.e("김영호 에러", new String(htmlBodyBytes), error);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                resultCallback.notifyError(error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                L.e("");
                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json;charset=utf-8”");
//                params.put("Accept","application/json");
                params.put("appKey", "eeb8e205-15f3-47d3-817b-99d83af8c8ad");
//                params.put("Accept-Language", "ko");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                L.e("");
                Map<String, String> params = new HashMap<>();
                params.put("endX", String.valueOf(endLocation.getLongitude()));
                params.put("endY", String.valueOf(endLocation.getLatitude()));
                params.put("startX", String.valueOf(startLocation.getLongitude()));
                params.put("startY", String.valueOf(startLocation.getLatitude()));
                params.put("reqCoordType", "WGS84GEO");
                params.put("resCoordType", "WGS84GEO");
                params.put("tollgateFareOption", "2");
                params.put("roadType", "32");
                params.put("searchOption", "0");
                params.put("trafficInfo", "Y");
                params.put("gpsTime", "10000");

                return params;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                L.e("[parseNetworkResponse] " + response);
                try {
                    String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(json), HttpHeaderParser.parseCacheHeaders(response));

                } catch (UnsupportedEncodingException e) {
                    L.e("[parseNetworkResponse] " + e.getMessage());
                    return Response.error(new ParseError(e));
                } catch (JsonSyntaxException e) {
                    L.e("[JsonSyntaxException] " + e.getMessage());
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    L.e("[JSONException] " + e.getMessage());
                    return Response.error(new ParseError(e));
                } catch (Exception e) {
                    L.e("[Exception] " + e.getMessage());
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                resultCallback.notifySuccess("", response);
            }
        };
        requestQueue.add(req);
    }

    public void getGeoWTM(Location location) {
        //위치정보 좌표를 TM좌표로 변환한다.
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);


        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?" + "x=" + location.getLongitude() + "&y=" + location.getLatitude();

        L.e("::::getGeoWTM URL : " + url);

        Request<JSONObject> req = new Request<JSONObject>(Request.Method.GET, url, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultCallback.notifyError(error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "KakaoAK " + "261b5c63a15536286de812713e966601");
                return params;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String json = new String(
                            response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(json), HttpHeaderParser.parseCacheHeaders(response));

                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JsonSyntaxException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                resultCallback.notifySuccess("", response);
            }
        };
        requestQueue.add(req);
    }

}
