package com.palmscanner.pos;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.palmscanner.pos.callback.PalmRegistrationCallback;
import com.palmscanner.pos.threads.PalmRegistrationThread;
import com.palmscanner.pos.viewmodel.RegisterViewModel;
import com.palmscanner.pos.viewmodel.datatype.PalmMaskedImage;
import com.saintdeem.palmvein.SDPVUnifiedAPI;

import java.util.Base64;
import java.util.List;

import com.saintdeem.palmvein.util.Constant;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "__RegisterActivity__";
    private SDPVUnifiedAPI unifiedAPI;
    private RegisterViewModel mRegisterViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        unifiedAPI = SDPVUnifiedAPI.getInstance();


        //At last
        mRegisterViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    public void initSDK() {
        //TODO: init sdk
        unifiedAPI.initDevice(this);
        PalmRegistrationThread registrationThread = new PalmRegistrationThread(this, new PalmRegistrationCallback() {
            @Override
            public void onRegistrationSuccess(byte[] palmToken, List<byte[]> images) {
                String palmTokenBase64 = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    palmTokenBase64 = Base64.getEncoder().encodeToString(palmToken);
                }
                Log.d(TAG, "onRegistrationSuccess: " + palmTokenBase64);
            }

            @Override
            public void onRegistrationFailed(String errorMessage) {
                Log.d(TAG, "onRegistrationSuccess: " + errorMessage);
            }

            @Override
            public void onImageCaptured(byte[] imageBitmap) {
                mRegisterViewModel.setMaskedImage(new PalmMaskedImage(imageBitmap, Constant.CWidth, Constant.CHeight));
            }
        }, 20);
        registrationThread.start();
    }
}