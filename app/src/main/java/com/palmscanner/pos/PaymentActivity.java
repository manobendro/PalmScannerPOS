package com.palmscanner.pos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.palmscanner.pos.callback.PalmVerificationCallback;
import com.palmscanner.pos.database.PosSqliteDB;
import com.palmscanner.pos.database.model.User;
import com.palmscanner.pos.fragments.PaymentAmount;
import com.palmscanner.pos.fragments.PaymentStatus;
import com.palmscanner.pos.fragments.PaymentVerifyPalm;
import com.palmscanner.pos.threads.PalmVerificationThread;
import com.palmscanner.pos.utils.ApiHelper;
import com.palmscanner.pos.viewmodel.PaymentViewModel;
import com.palmscanner.pos.viewmodel.datatype.PalmMaskedImage;
import com.saintdeem.palmvein.util.Constant;

public class PaymentActivity extends AppCompatActivity {

    public static final String TAG = "__PaymentActivity__";
    protected static final int cameraWidth = Constant.CWidth;
    protected static final int cameraHeight = Constant.CHeight;
    private PaymentViewModel mPaymentViewModel;
    private PalmVerificationThread mPalmVerificationThread;

    private PosSqliteDB posSqliteDB;

    public PaymentActivity() {
        super(R.layout.activity_payment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().setAttributes(attributes);

        posSqliteDB = new PosSqliteDB(this);
        mPaymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        mPaymentViewModel.getPaymentItem().observe(this, paymentItem -> {
            Log.d(TAG, "PaymentItem: " + paymentItem.toString());
            if (paymentItem.getAmount() > 0) {
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.fragment_payment_container_view, PaymentVerifyPalm.class, null).commit();
                }
                startPalmVerification();
            } else {
                Toast.makeText(this, "Put amount more than zero.", Toast.LENGTH_SHORT).show();
            }
        });

//        //For palm verification testing
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.fragment_payment_container_view, PaymentVerifyPalm.class, null).commit();
//        }
//        startPalmVerification();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.fragment_payment_container_view, PaymentAmount.class, null).commit();
        }


    }

    public void startPalmVerification() {
        this.mPalmVerificationThread = new PalmVerificationThread(this, new PalmVerificationCallback() {

            @Override
            public void onVerificationSuccess(String palmToken, String successMessage) {

                User user = posSqliteDB.getUserByUuid(palmToken);

                Log.d("TAG_PAY", "Card number: "+user.getCardNumber());

                ApiHelper.postData("https://posbackend-x0yp.onrender.com/api/payment/pay", user.getCardNumber(), null, new ApiHelper.Callback(){

                    @Override
                    public void onSuccess(String response) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(PaymentStatus.ARG_SUCCESS, true);
                        bundle.putString(PaymentStatus.ARG_MSG, "Payment success.");

                        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_payment_container_view, PaymentStatus.class, bundle).commit();
                        Log.d(TAG, String.format("onVerificationSuccess: TOKEN: %s, MSG: %s", palmToken, successMessage));
                    }

                    @Override
                    public void onFailure(String error) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(PaymentStatus.ARG_SUCCESS, false);
                        bundle.putString(PaymentStatus.ARG_MSG, "Payment failed.");

                        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_payment_container_view, PaymentStatus.class, bundle).commit();
                        Log.d(TAG, String.format("onVerificationFailed: TOKEN: %s, MSG: %s", palmToken, successMessage));
                    }
                });

//                Bundle bundle = new Bundle();
//                bundle.putBoolean(PaymentStatus.ARG_SUCCESS, true);
//                bundle.putString(PaymentStatus.ARG_MSG, "Payment success.");
//
//                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_payment_container_view, PaymentStatus.class, bundle).commit();
//                Log.d(TAG, String.format("onVerificationSuccess: TOKEN: %s, MSG: %s", palmToken, successMessage));
            }

            @Override
            public void onVerificationFailed(String errorMessage) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(PaymentStatus.ARG_SUCCESS, false);
                bundle.putString(PaymentStatus.ARG_MSG, "Payment failed.");

                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_payment_container_view, PaymentStatus.class, bundle).commit();
                Log.d(TAG, "onVerificationFailed: " + errorMessage);
            }

            @Override
            public void onImageCaptured(byte[] imageBitmap) {
                runOnUiThread(() -> {
                    mPaymentViewModel.setPalmMaskedImage(new PalmMaskedImage(imageBitmap, cameraWidth, cameraHeight));
                });
            }
        }, 30 * 1000);
        this.mPalmVerificationThread.start();

    }

    public void stopPalmVerification() {
        if (this.mPalmVerificationThread != null) {
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