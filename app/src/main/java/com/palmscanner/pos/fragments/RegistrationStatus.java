package com.palmscanner.pos.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.palmscanner.pos.MainActivity;
import com.palmscanner.pos.R;
import com.palmscanner.pos.RegisterActivity;


public class RegistrationStatus extends Fragment {


    public static final String ARG_SUCCESS = "SUCCESS";
    public static final String ARG_MSG = "MSG";


    private boolean isSuccess = false;
    private String msg = "Registration Failed!";

    private ImageView statusIcon;
    private Button retryButton;
    private Button homeButton;
    private TextView statusMsg;

    public RegistrationStatus() {
        super(R.layout.fragment_registration_status);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isSuccess = getArguments().getBoolean(ARG_SUCCESS);
            msg = getArguments().getString(ARG_MSG);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusIcon = view.findViewById(R.id.reg_status_icon);
        retryButton = view.findViewById(R.id.reg_status_button_retry);
        homeButton = view.findViewById(R.id.reg_status_button_home);
        statusMsg = view.findViewById(R.id.reg_status_msg);

        if (isSuccess) {
            statusIcon.setImageResource(R.drawable.success);
        } else {
            statusIcon.setImageResource(R.drawable.failed);
        }
        statusMsg.setText(msg);

        if (isSuccess) {
            retryButton.setVisibility(View.GONE);
        }

        retryButton.setOnClickListener((v) -> {
            assert getActivity() != null;
            getActivity().startActivity(new Intent(getActivity(), RegisterActivity.class));
            getActivity().finish();
        });
        homeButton.setOnClickListener((v) -> {
            assert getActivity() != null;
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });


    }
}