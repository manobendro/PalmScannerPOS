package com.palmscanner.pos.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.palmscanner.pos.R;
import com.palmscanner.pos.viewmodel.PaymentViewModel;
import com.saintdeem.palmvein.util.BitmapUtil;

public class PaymentVerifyPalm extends Fragment {

    private PaymentViewModel paymentViewModel;
    private ImageView palmImageView;

    public PaymentVerifyPalm() {
        super(R.layout.fragment_pay_verify_palm);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        paymentViewModel = new ViewModelProvider(requireActivity()).get(
                PaymentViewModel.class
        );
        //Init view
        palmImageView = view.findViewById(R.id.payment_palm_image_view);
        palmImageView.requestFocus();

        // Init image view then observe masked image
        paymentViewModel.getPalmMaskedImage().observe(getViewLifecycleOwner(), maskedImage -> {
            if (palmImageView != null && maskedImage != null) {
                palmImageView.setImageBitmap(BitmapUtil.coverToBitmap(maskedImage.getMaskedImage(), maskedImage.getWidth(), maskedImage.getHeight()));
            }
        });
    }

    @NonNull
    @Override
    public String toString() {
        return paymentViewModel.toString();
    }
}
