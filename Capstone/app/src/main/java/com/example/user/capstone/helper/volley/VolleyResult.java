package com.example.user.capstone.helper.volley;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface VolleyResult {
    void notifySuccess(String type, JSONObject response);

    void notifyError(VolleyError error);
}
