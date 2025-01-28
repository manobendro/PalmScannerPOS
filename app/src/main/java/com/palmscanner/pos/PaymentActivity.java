package com.palmscanner.pos;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.palmscanner.pos.callback.PalmVerificationCallback;
import com.palmscanner.pos.fragments.PaymentAmount;
import com.palmscanner.pos.viewmodel.PaymentViewModel;
import com.saintdeem.palmvein.SDPVUnifiedAPI;
import com.saintdeem.palmvein.device.bean.CaptureResult;
import com.saintdeem.palmvein.device.bean.DeviceMsg;
import com.saintdeem.palmvein.service.SDPVServiceConstant;
import com.saintdeem.palmvein.service.bean.DetectRoiResult;
import com.saintdeem.palmvein.service.bean.ServiceMsg;
import com.saintdeem.palmvein.util.Constant;

public class PaymentActivity extends AppCompatActivity {

    private PaymentViewModel mPaymentViewModel;
    public static final String TAG = "__PaymentActivity__";
    protected static final int cameraWidth = Constant.CWidth;
    protected static final int cameraHeight = Constant.CHeight;

    public PaymentActivity() {
        super(R.layout.activity_payment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_payment_container_view, PaymentAmount.class, null)
                    .commit();
        }

        mPaymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        mPaymentViewModel.getPaymentItem().observe(this, paymentItem -> {
            Log.d(TAG, "PaymentItem: " + paymentItem.toString());
        });
    }

    public void startPalmVerification() {

    }

    public void stopPalmVerification() {
    }

    @NonNull
    @Override
    public String toString() {
        return mPaymentViewModel.toString();
    }
}