package com.example.user.capstone.helper;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.example.user.capstone.MatchingMapActivity;
import com.example.user.capstone.MatchingOptionActivity;
import com.example.user.capstone.model.TMapInfo;


public class ActivityLauncher {
    //다른 Activity로 전환할떄 사용하는 class 이다

    public static int REQUEST_CODE_NONE = -1;

    private Fragment fragment = null;
    private Activity activity = null;

    public ActivityLauncher(@NonNull Fragment frag) {
        fragment = frag;
    }

    public ActivityLauncher(Activity activity) {
        fragment = null;
        this.activity = activity;
    }



    public void initMatchingOptionActivity(AnimationDirection.Direction direction, TMapInfo tMapInfo) {
        //메인화면으로 전환 시키는 함수이다.
        if (fragment != null) {
            activity = fragment.getActivity();
        }
        Intent intent = new Intent(activity, MatchingOptionActivity.class);
        intent.putExtra("TMapInfo",tMapInfo);

        if (fragment != null) {
            fragment.startActivityForResult(intent,MatchingMapActivity.REQUEST_CODE);
            fragment.getActivity().overridePendingTransition(direction.getEnterAni(), direction.getExitAni());
        } else {
            activity.startActivityForResult(intent,MatchingMapActivity.REQUEST_CODE);
            activity.overridePendingTransition(direction.getEnterAni(), direction.getExitAni());
        }
    }




}

