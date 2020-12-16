package com.example.user.capstone.helper.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.capstone.R;
import com.example.user.capstone.helper.L;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WarningDialog extends BottomSheetDialogFragment {


    private static WarningDialog Instance;

    private String mDistance = "";
    private String mAmount = "";


    public static synchronized WarningDialog getInstance() {
        if (Instance == null) {
            Instance = new WarningDialog();
        }
        return Instance;
    }

    public WarningDialog() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
        setCancelable(false);
    }

    @BindView(R.id.tv_distance)
    TextView tvDistance;

    @BindView(R.id.tv_paitent_amonut)
    TextView tvWarning;


    @BindView(R.id.view_paitent_container)
    LinearLayout viewPaitentContainer;


    @BindView(R.id.view_empty_container)
    LinearLayout viewEmptyContainer;


    @OnClick(R.id.btn_one)
    void onClose() {
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.warning_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        tvDistance.setText("총거리 : " + mDistance);

        if (!mAmount.equalsIgnoreCase("0")) {
            viewPaitentContainer.setVisibility(View.VISIBLE);
            tvWarning.setText(mAmount);
        } else {
            viewEmptyContainer.setVisibility(View.VISIBLE);
        }
    }

    public void setDialogText(String distance, String amount) {
        this.mDistance = distance;
        this.mAmount = amount;
    }
}
