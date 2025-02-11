package com.palmscanner.pos.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.palmscanner.pos.AppConstant;
import com.palmscanner.pos.R;
import com.palmscanner.pos.database.PosSqliteDB;
import com.palmscanner.pos.database.model.User;
import com.palmscanner.pos.threads.HttpPollingThread;
import com.palmscanner.pos.viewmodel.RegisterViewModel;
import com.palmscanner.pos.viewmodel.datatype.PalmDataItem;
import com.saintdeem.palmvein.SDPVUnifiedAPI;

public class QrCodeFragment extends Fragment {
    private RegisterViewModel mRegisterViewModel;

    public QrCodeFragment() {
        super(R.layout.fragment_qr_code);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Required activity
        assert getActivity() != null;

        // init view model
        this.mRegisterViewModel = new ViewModelProvider(getActivity()).get(RegisterViewModel.class);
        ImageView imageViewQrCode = view.findViewById(R.id.reg_qrcode);

        // get palm data from view model
        PalmDataItem palmData = this.mRegisterViewModel.getPalmData().getValue();

        //if any data
        if (palmData != null && palmData.getUuid() != null) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(palmData.getUuid(), BarcodeFormat.QR_CODE, 400, 400);
                imageViewQrCode.setImageBitmap(bitmap);
            } catch (Exception e) {

            }
        }

//        //if palm data changed
//        this.mRegisterViewModel.getPalmData().observe(getActivity(), palm -> {
//            if (palm != null || palm.getUuid() != null) {
//                try {
//                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//                    Bitmap bitmap = barcodeEncoder.encodeBitmap(palm.getUuid(), BarcodeFormat.QR_CODE, 400, 400);
//
//                    imageViewQrCode.setImageBitmap(bitmap);
//                } catch (Exception e) {
//
//                }
//            }
//
//        });
        (new HttpPollingThread(AppConstant.API_DOMAIN + "/api/devices?deviceId=" + palmData.getUuid(), 40, new HttpPollingThread.Callback() {
            @Override
            public void onSuccess(String token) {
                Log.d("QR", token);

                //Inserting data to in memory cache pool
                SDPVUnifiedAPI.getInstance().insertPalm(palmData.getPalmToken(), palmData.getUuid());

                // Inserting data to database
                PosSqliteDB db = new PosSqliteDB(getActivity());
                String palmTokenBase64 = Base64.encodeToString(palmData.getPalmToken(), Base64.DEFAULT);
                int random = (int) (Math.random() * 1000);

                Log.d("QR", palmTokenBase64);
                Log.d("QR", palmData.getUuid());
                Log.d("QR", token);

                db.addUser(new User(palmData.getUuid(), palmTokenBase64, token, "CED+" + Math.abs(random), "CCCV+" + Math.abs(random), "CHN+" + Math.abs(random), "MASTER"));
                db.closeDatabase();

                Bundle bundle = new Bundle();
                bundle.putBoolean(RegistrationStatus.ARG_SUCCESS, true);
                bundle.putString(RegistrationStatus.ARG_MSG, "User registration success.");
                getActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_register_container_view, RegistrationStatus.class, bundle).commit();

            }

            @Override
            public void onFailure(String error) {
                Log.d("QR", error);
                Bundle bundle = new Bundle();
                bundle.putBoolean(RegistrationStatus.ARG_SUCCESS, false);
                bundle.putString(RegistrationStatus.ARG_MSG, "User registration failed.");
                getActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_register_container_view, RegistrationStatus.class, bundle).commit();

            }
        })).start();
    }
}
