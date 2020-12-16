package com.example.user.capstone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.user.capstone.helper.ActivityLauncher;
import com.example.user.capstone.helper.AnimationDirection;
import com.example.user.capstone.helper.L;
import com.example.user.capstone.helper.contract.SearchContract;
import com.example.user.capstone.helper.presenter.MapSearchPresenter;
import com.example.user.capstone.helper.rx.RxHelper;
import com.example.user.capstone.helper.rx.RxTaskCall;
import com.example.user.capstone.map.Mapfragment;
import com.example.user.capstone.model.Site;
import com.example.user.capstone.model.TMapInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class MatchingMapActivity extends AppCompatActivity implements SearchContract.View, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static int REQUEST_CODE = 49037;
    public static int AUTOCOMPLETE_REQUEST_CODE = 49030;

    @BindView(R.id.my_address)
    AutoCompleteTextView mMyAddressTextView;

    @BindView(R.id.editText)
    AutoCompleteTextView mAutoCompleteTextView;

    @BindView(R.id.btn_confirm)
    Button mConfirmButton;


    private GoogleApiClient mGoogleApiClient;
    private Mapfragment mMapfragment;
    private FragmentManager mChiFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private MapSearchPresenter mPresenter;
    private TMapInfo mCurrentTMapInfo;
    private Location mCurrentLocation;
    private ProgressDialog mProgressDialog;
    private ActivityLauncher mActivityLauncher = new ActivityLauncher(this);
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Site mCurrentSite = null;


    @OnClick(R.id.btn_confirm)
    public void clickMatchingCreate() {
        mActivityLauncher.initMatchingOptionActivity(AnimationDirection.Direction.ENTER_RIGHT, mCurrentTMapInfo);
    }

    @OnClick(R.id.btn_cancel)
    public void clickMatchingCancel() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        //Mvp 패턴 적용 Activity에는 View적인 요소만 사용합니다. 로직관련은 Presenter로 이관..
        mPresenter = new MapSearchPresenter(getApplicationContext(), this);

        //구글 Api를 불러온다.
        setBuildGoogleApi();

        //지도 관련 로직을 추가 한다.
        //Google Map 객체를 불러온후 화면에 셋팅을해준다.
        mMapfragment = Mapfragment.getInstance();
        mChiFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mChiFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_placeholder, mMapfragment);
        mFragmentTransaction.commitAllowingStateLoss();

        mMapfragment.setCameraMoveCompleteListener(mCameraMoveCompleteListener);

        //Presenter로 직접적인 컨트롤러 부분을 다 이관한다 . SearchPresenter.class 를 확인해주세요
        mPresenter.onLoad();
        mPresenter.setMapFragemnt(mMapfragment);
        mConfirmButton.setEnabled(false);


        getJsonData();


    }

    private void getJsonData() {
        if (mCurrentSite == null) {
            showProgressDialog("위치정보 불러오는중");
            compositeDisposable.add(RxHelper.runOnBackground(new RxTaskCall<Site>() {
                @Override
                public Site doInBackground() throws Exception {
                    return getReadData();
                }

                @Override
                public void onResult(Site result) {
                    L.e(":::::result : " + result);
                    mCurrentSite = result;
                    mMapfragment.setSite(result);
                    hideProgressDialog();
                }

                @Override
                public void onError(Throwable e) {
                    L.e("::::::e " + e.getMessage());
                    hideProgressDialog();
                }
            }));
        }


    }

    private Site getReadData() throws Exception {
        //데이터를 asset의 json 파일에서 읽어들이기 위한 함수.
        AssetManager assetManager = getResources().getAssets();

        InputStream is = assetManager.open("corona.json");
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader reader = new BufferedReader(isr);

        StringBuffer buffer = new StringBuffer();
        String line = reader.readLine();
        while (line != null) {
            buffer.append(line + "\n");
            line = reader.readLine();
        }
        String json = buffer.toString();
        return new Gson().fromJson(json, Site.class);
    }


    private Mapfragment.CameraMoveCompleteListener mCameraMoveCompleteListener = new Mapfragment.CameraMoveCompleteListener() {
        @Override
        public void animateCameraComplete(Location location) {
            L.i("animateCameraComplete");
            //테스트 위치정보... 사용하지않음.
            Location test = new Location("test");
            test.setLatitude(37.2210543);
            test.setLongitude(127.0734669);


            //성공적으로 목적지정보를 불러올시 추천경로를 사용한다.
            mPresenter.getTMapRoute(mCurrentLocation, location, mCurrentSite);
        }
    };


    private synchronized void setBuildGoogleApi() {
        //구글맵 클라이언트생성
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //백버튼 누르면 EXIT_RIGHT 오른쪽으로 퇴장하는 애니메이션효과로 해당 화면을 종료한다
        finishWithAni(AnimationDirection.Direction.EXIT_RIGHT);
    }

    public void finishWithAni(AnimationDirection.Direction direction) {
        finish();
        overridePendingTransition(direction.getEnterAni(), direction.getExitAni());
    }


    @Override
    public void setLoading(boolean loading) {
        //위치정보를 불러오는 동안 데이터양에 따라 로딩이 생기니 이부분에서 로딩처리
        if (loading) {
            mConfirmButton.setEnabled(false);
            showProgressDialog("추천경로 로드중");
        } else {
            mConfirmButton.setEnabled(true);
            hideProgressDialog();
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setOnTouchListener(View.OnTouchListener I) {
        mAutoCompleteTextView.setOnTouchListener(I);
    }

    @Override
    public void setMyAddressOnTouchListener(View.OnTouchListener I) {
        mMyAddressTextView.setOnTouchListener(I);
    }


    @Override
    public void setFindPlace(com.google.android.libraries.places.api.model.Place place, String city) {
        if (mMapfragment != null) {
            hideKeyboard();
            mMapfragment.updateCamera(place, city);
        }
    }

    @Override
    public void setPreditInfo(TMapInfo info) {
        mCurrentTMapInfo = info;
    }


    @Override
    public void setTMapFail(String msg) {
        L.i("[setTMapFail] : " + msg);
    }

    @Override
    public void setCurrentLocation(String address, Location location) {
        this.mCurrentLocation = location;
        runOnUiThread(() -> mMyAddressTextView.setText(address));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        L.i(":::mGoogleApiClient onConnected");
        if (mGoogleApiClient != null) {
            mPresenter.onBuildGoogleClient(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        L.e("::::::connectionResult " + connectionResult.getErrorMessage());
    }

    public void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            String serviceName = Context.INPUT_METHOD_SERVICE;
            InputMethodManager imm = (InputMethodManager) getSystemService(serviceName);
            int stateHide = InputMethodManager.HIDE_NOT_ALWAYS;
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), stateHide);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MatchingMapActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                L.i("Place: " + place.getName() + ", " + place.getId() + " , " + place.getLatLng());
                mPresenter.findPlace(place);
                runOnUiThread(() -> mAutoCompleteTextView.setText(place.getName()));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                L.i(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void showProgressDialog(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle(text);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }


    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!compositeDisposable.isDisposed()) compositeDisposable.dispose();
    }
}
