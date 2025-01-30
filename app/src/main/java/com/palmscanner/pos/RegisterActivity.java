package com.palmscanner.pos;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.palmscanner.pos.callback.PalmRegistrationCallback;
import com.palmscanner.pos.database.PosSqliteDB;
import com.palmscanner.pos.database.model.User;
import com.palmscanner.pos.fragments.RegisterNFC;
import com.palmscanner.pos.fragments.RegisterPalm;
import com.palmscanner.pos.fragments.RegistrationStatus;
import com.palmscanner.pos.threads.PalmRegistrationThread;
import com.palmscanner.pos.viewmodel.RegisterViewModel;
import com.palmscanner.pos.viewmodel.datatype.PalmMaskedImage;
import com.palmscanner.pos.viewmodel.datatype.RegistrationStatusItem;
import com.saintdeem.palmvein.SDPVUnifiedAPI;
import com.saintdeem.palmvein.util.Constant;

import java.util.List;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "__RegisterActivity__";
    private SDPVUnifiedAPI unifiedAPI;
    private RegisterViewModel mRegisterViewModel;
    private PalmRegistrationThread registrationThread;
    private PosSqliteDB mPosSqliteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().setAttributes(attributes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        unifiedAPI = SDPVUnifiedAPI.getInstance();
        mPosSqliteDB = new PosSqliteDB(this);

        //At last
        mRegisterViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        this.initSDK();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.fragment_register_container_view, RegisterNFC.class, null).commit();
        }

        mRegisterViewModel.getBankCardData().observe(this, bankCardItem -> {

//            Log.d("_BANK_CARD_",bankCardItem.getCardNo());
            if(bankCardItem.getCardNo() != null) {
                //NOTES: If user scan a valid nfc enabled bank card then palm scan start
                if(!bankCardItem.getCardNo().isEmpty()){
                    Log.d(TAG, "Card NO: "+bankCardItem.getCardNo());
                    if (savedInstanceState == null) {
                        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.fragment_register_container_view, RegisterPalm.class, null).commit();
                    }
                    registrationThread.start();
                }
            }else {
                Log.d(TAG, "NFC READ Error. ");
                // if no valid bank card then registration failed
                Bundle bundle = new Bundle();
                bundle.putBoolean(RegistrationStatus.ARG_SUCCESS, false);
                bundle.putString(RegistrationStatus.ARG_MSG, "User registration failed.");
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_register_container_view, RegistrationStatus.class, bundle).commit();
            }
        });
    }

    public void initSDK() {
        unifiedAPI.initDevice(this);
        registrationThread = new PalmRegistrationThread(this, new PalmRegistrationCallback() {
            @Override
            public void onRegistrationSuccess(byte[] palmToken, List<byte[]> images) {
                registrationThread.stopRegistration();
                //Inserting data to in memory cache pool
                String uuid = UUID.randomUUID().toString();
                try {
                    unifiedAPI.insertPalm(palmToken, uuid);
                } catch (Exception e) {
                    Log.e(TAG, "Palm insertion failed may be user already registered.\n" + e.getMessage());
                }


                String palmTokenBase64 = Base64.encodeToString(palmToken, Base64.DEFAULT);


                int randon = (int) (Math.random() * 1000);
                mPosSqliteDB.addUser(new User(uuid, palmTokenBase64, "CN+" + Math.abs(randon), "CED+" + Math.abs(randon), "CCCV+" + Math.abs(randon), "CHN+" + Math.abs(randon), "MASTER"));
                Log.d(TAG, "onRegistrationSuccess: " + palmTokenBase64);

                runOnUiThread(() -> {
                    mRegisterViewModel.setRegistrationStatus(new RegistrationStatusItem(true, "User registration success."));
                });


                Bundle bundle = new Bundle();
                bundle.putBoolean(RegistrationStatus.ARG_SUCCESS, true);
                bundle.putString(RegistrationStatus.ARG_MSG, "User registration success.");

                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_register_container_view, RegistrationStatus.class, bundle).commit();

            }

            @Override
            public void onRegistrationFailed(String errorMessage) {
                registrationThread.stopRegistration();
                Log.d(TAG, "onRegistrationFailed: " + errorMessage);
                Bundle bundle = new Bundle();
                bundle.putBoolean(RegistrationStatus.ARG_SUCCESS, false);
                bundle.putString(RegistrationStatus.ARG_MSG, "User registration failed.");

                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_register_container_view, RegistrationStatus.class, bundle).commit();
            }

            @Override
            public void onImageCaptured(byte[] imageBitmap) {
                runOnUiThread(() -> {
                    if (mRegisterViewModel != null) {
                        mRegisterViewModel.setMaskedImage(new PalmMaskedImage(imageBitmap, Constant.CWidth, Constant.CHeight));
                    }
                });
            }
        }, 30 * 1000); //10000 ms for 10s timeout
//        registrationThread.start();
    }

    @Override
    protected void onDestroy() {
        if (this.registrationThread != null) {
            this.registrationThread.stopRegistration();
        }
        if (this.mPosSqliteDB != null) {
            this.mPosSqliteDB.closeDatabase();
        }
        super.onDestroy();
    }
}