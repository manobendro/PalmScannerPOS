package com.palmscanner.pos.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.palmscanner.pos.R;
import com.palmscanner.pos.viewmodel.RegisterViewModel;
import com.saintdeem.palmvein.util.BitmapUtil;

public class RegisterPalm extends Fragment {
    private RegisterViewModel mRegisterViewModel;
    private ImageView palmImageView;

    public RegisterPalm() {
        super(R.layout.fragment_reg_palm);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRegisterViewModel = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);


        palmImageView = view.findViewById(R.id.register_palm_image_view);

        mRegisterViewModel.getMaskedImage().observe(getViewLifecycleOwner(), maskedImage -> {
            if (maskedImage != null) {
                palmImageView.setImageBitmap(BitmapUtil.coverToBitmap(maskedImage.getMaskedImage(), maskedImage.getWidth(), maskedImage.getHeight()));
            }
        });
    }
}
