package com.palmscanner.pos;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.palmscanner.pos.callback.PalmVerificationCallback;
import com.palmscanner.pos.fragments.PaymentAmount;
import com.palmscanner.pos.fragments.PaymentStatus;
import com.palmscanner.pos.fragments.PaymentVerifyPalm;
import com.palmscanner.pos.fragments.RegistrationStatus;
import com.palmscanner.pos.threads.PalmVerificationThread;
import com.palmscanner.pos.viewmodel.PaymentViewModel;
import com.palmscanner.pos.viewmodel.datatype.PalmMaskedImage;
import com.saintdeem.palmvein.SDPVUnifiedAPI;
import com.saintdeem.palmvein.device.bean.CaptureResult;
import com.saintdeem.palmvein.device.bean.DeviceMsg;
import com.saintdeem.palmvein.service.SDPVServiceConstant;
import com.saintdeem.palmvein.service.bean.DetectRoiResult;
import com.saintdeem.palmvein.service.bean.ServiceMsg;
import com.saintdeem.palmvein.util.BitmapUtil;
import com.saintdeem.palmvein.util.Constant;

public class PaymentActivity extends AppCompatActivity {

    private PaymentViewModel mPaymentViewModel;
    public static final String TAG = "__PaymentActivity__";
    protected static final int cameraWidth = Constant.CWidth;
    protected static final int cameraHeight = Constant.CHeight;

    private PalmVerificationThread mPalmVerificationThread;

    public PaymentActivity() {
        super(R.layout.activity_payment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mPaymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        mPaymentViewModel.getPaymentItem().observe(this, paymentItem -> {
            Log.d(TAG, "PaymentItem: " + paymentItem.toString());
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_payment_container_view, PaymentVerifyPalm.class, null)
                    .commit();
        }
        startPalmVerification();
    }

    public void startPalmVerification() {
        this.mPalmVerificationThread = new PalmVerificationThread(this,
                new PalmVerificationCallback(){

                    @Override
                    public void onVerificationSuccess(String palmToken, String successMessage) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(PaymentStatus.ARG_SUCCESS, true);
                        bundle.putString(PaymentStatus.ARG_MSG, "Payment success.");

                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragment_payment_container_view, PaymentStatus.class, bundle)
                                .commit();
                        Log.d(TAG, String.format("onVerificationSuccess: TOKEN: %s, MSG: %s", palmToken, successMessage));
                    }

                    @Override
                    public void onVerificationFailed(String errorMessage) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(PaymentStatus.ARG_SUCCESS, false);
                        bundle.putString(PaymentStatus.ARG_MSG, "Payment failed.");

                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragment_payment_container_view, PaymentStatus.class, bundle)
                                .commit();
                        Log.d(TAG, "onVerificationFailed: " + errorMessage);
                    }

                    @Override
                    public void onImageCaptured(byte[] imageBitmap) {
                        runOnUiThread(()->{
                            mPaymentViewModel.setPalmMaskedImage(new PalmMaskedImage(imageBitmap, cameraWidth, cameraHeight));
                        });
                    }
                }, 30 * 1000
        );
        this.mPalmVerificationThread.start();

    }

    public void stopPalmVerification() {
        if(this.mPalmVerificationThread != null) {
            this.mPalmVerificationThread.stopVerification();
        }
    }

    @Override
    protected void onDestroy() {
        stopPalmVerification();
        super.onDestroy();
    }

    @NonNull
    @Override
    public String toString() {
        return mPaymentViewModel.toString();
    }
}