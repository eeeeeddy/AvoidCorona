package com.example.user.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.user.capstone.helper.L;
import com.example.user.capstone.helper.rx.RxCall;
import com.example.user.capstone.helper.rx.RxHelper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);


        //1.5초후 다음화면진행.
        RxHelper.delay(1500, data -> permissionCheck());


    }


    private void permissionCheck() {
        L.i("::ㄱㄱ");
        //지도나 위치정보 관련 을 사용하려면 권한 체크를 하여야한다 Dexter 라이브러리를 통해 권한체크를 시도.
        Dexter.withActivity(SplashActivity.this).withPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    startActivity(new Intent(SplashActivity.this, MatchingMapActivity.class));
                    finish();
                } else {
                    finish();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                //권한체크 실패시 화면 전환 시키지않는다.
                token.continuePermissionRequest();
            }
        }).check();
    }
}
