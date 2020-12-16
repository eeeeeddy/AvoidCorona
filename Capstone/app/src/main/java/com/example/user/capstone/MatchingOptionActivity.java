package com.example.user.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.user.capstone.helper.ActivityLauncher;
import com.example.user.capstone.helper.AnimationDirection;
import com.example.user.capstone.helper.ImageListAdapter;
import com.example.user.capstone.helper.L;
import com.example.user.capstone.model.TMapInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MatchingOptionActivity extends AppCompatActivity {
    public static int REQUEST_CODE = 49038;

    @BindView(R.id.rvlist_menu_image)
    RecyclerView mMenuImageListView;

    @BindView(R.id.predit)
    TextView tvPreditInfo;

    private ImageListAdapter mImageListAdapter;
    private LinearLayoutManager linearLayoutManager;



    @OnClick(R.id.option_cancel)
    public void clickMatchingCancel() {
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        L.i("intent: " + intent);
        if (intent != null) {
            //성공적으로 예상거리 시간 금액정보를 받아왔다면?
            TMapInfo currentTMapInfo = (TMapInfo) intent.getSerializableExtra("TMapInfo");
            L.i("currentTMapInfo: " + currentTMapInfo);
            if (currentTMapInfo != null) {
                L.e("currentTMapInfo : " + currentTMapInfo.toString());
                StringBuilder builder = new StringBuilder();
                double distance = Double.valueOf(currentTMapInfo.getTotalDistance()) / 1000;
                int time = Integer.valueOf(currentTMapInfo.getTotalTime()) / 60;
                builder.append("예상거리 : ").append(distance).append(" 예상시간").append(time + "분").append(" 예상금액 : ").append(currentTMapInfo.getTaxiFare());
                tvPreditInfo.setText(builder.toString());
            }
        }


        //mImageListAdapter 는 이미지를 보여주는 Listview 이다
        mImageListAdapter = new ImageListAdapter(getApplicationContext());


        //가로로 전환시킬수있게  LinearLayoutManager.HORIZONTAL 처리하였다.
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mMenuImageListView.setHasFixedSize(true);
        mMenuImageListView.setAdapter(mImageListAdapter);
        mMenuImageListView.setLayoutManager(linearLayoutManager);
        mMenuImageListView.setNestedScrollingEnabled(false);


        //test 이미지
        ArrayList<Integer> dummyImage = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            //현재 ic_launcher 이미지를 넣는다.
            dummyImage.add(R.mipmap.ic_launcher);
        }


        //이미지넣은 데이터를 addInsert 해준다
        mImageListAdapter.addInsert(dummyImage);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MatchingOptionActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
