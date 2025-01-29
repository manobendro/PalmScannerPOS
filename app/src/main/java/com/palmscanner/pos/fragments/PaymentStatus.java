package com.palmscanner.pos.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.palmscanner.pos.MainActivity;
import com.palmscanner.pos.PaymentActivity;
import com.palmscanner.pos.R;

public class PaymentStatus extends Fragment {

    public static final String ARG_SUCCESS = "SUCCESS";
    public static final String ARG_MSG = "MSG";


    private boolean isSuccess = false;
    private String msg = "Registration Failed!";

    private ImageView statusIcon;
    private Button retryButton;
    private Button homeButton;
    private TextView statusMsg;

    public PaymentStatus(){
        super(R.layout.fragment_pay_status);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isSuccess = getArguments().getBoolean(ARG_SUCCESS);
            msg = getArguments().getString(ARG_MSG);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statusIcon = view.findViewById(R.id.pay_status_icon);
        retryButton = view.findViewById(R.id.pay_status_button_retry);
        homeButton = view.findViewById(R.id.pay_status_button_home);
        statusMsg = view.findViewById(R.id.pay_status_msg);

        Drawable icon;
        if (isSuccess) {
            icon = ResourcesCompat.getDrawable(requireContext().getResources(),
                    R.drawable.success,
                    requireContext().getTheme());
        } else {
            icon = ResourcesCompat.getDrawable(requireContext().getResources(),
                    R.drawable.failed,
                    requireContext().getTheme());
        }

        statusIcon.setImageDrawable(icon);
        statusMsg.setText(msg);

        if (isSuccess) {
            retryButton.setVisibility(View.GONE);
            homeButton.setVisibility(View.VISIBLE);
        }

        retryButton.setOnClickListener((v) -> {
            assert getActivity() != null;
            getActivity().startActivity(new Intent(getActivity(), PaymentActivity.class));
            getActivity().finish();
        });
        homeButton.setOnClickListener((v) -> {
            assert getActivity() != null;
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });
    }
}
