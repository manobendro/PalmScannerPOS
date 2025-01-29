package com.palmscanner.pos;

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
import com.palmscanner.pos.database.PosSqliteDB;
import com.palmscanner.pos.database.model.User;
import com.palmscanner.pos.fragments.RegisterPalm;
import com.palmscanner.pos.fragments.RegistrationStatus;
import com.palmscanner.pos.threads.PalmRegistrationThread;
import com.palmscanner.pos.viewmodel.RegisterViewModel;
import com.palmscanner.pos.viewmodel.datatype.PalmMaskedImage;
import com.palmscanner.pos.viewmodel.datatype.RegistrationStatusItem;
import com.saintdeem.palmvein.SDPVUnifiedAPI;

import java.util.Base64;
import java.util.UUID;
import java.util.List;

import com.saintdeem.palmvein.util.Constant;

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
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_register_container_view, RegisterPalm.class, null)
                    .commit();
        }
    }

    public void initSDK() {
        unifiedAPI.initDevice(this);
        registrationThread = new PalmRegistrationThread(this, new PalmRegistrationCallback() {
            @Override
            public void onRegistrationSuccess(byte[] palmToken, List<byte[]> images) {

                //Inserting data to in memory cache pool
                String uuid = UUID.randomUUID().toString();
                unifiedAPI.insertPalm(palmToken, uuid);


                String palmTokenBase64 = "ID" + System.currentTimeMillis();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    palmTokenBase64 = Base64.getEncoder().encodeToString(palmToken);
                }

                int randon = (int) (Math.random() * 1000);
                mPosSqliteDB.addUser(new User(uuid, palmTokenBase64,
                        "CN+" + Math.abs(randon),
                        "CED+" + Math.abs(randon),
                        "CCCV+" + Math.abs(randon),
                        "CHN+" + Math.abs(randon),
                        "MASTER"));
                Log.d(TAG, "onRegistrationSuccess: " + palmTokenBase64);

                mRegisterViewModel.setRegistrationStatus(new RegistrationStatusItem(true, "User registration success."));


                Bundle bundle = new Bundle();
                bundle.putBoolean("STATUS", true);
                bundle.putString("MSG", "User registration success.");

                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_register_container_view, RegistrationStatus.class, bundle)
                        .commit();

            }

            @Override
            public void onRegistrationFailed(String errorMessage) {
                Log.d(TAG, "onRegistrationFailed: " + errorMessage);
                Bundle bundle = new Bundle();
                bundle.putBoolean("STATUS", false);
                bundle.putString("MSG", "User registration failed.");

                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_register_container_view, RegistrationStatus.class, bundle)
                        .commit();
            }

            @Override
            public void onImageCaptured(byte[] imageBitmap) {
                runOnUiThread(() -> {
                    if (mRegisterViewModel != null) {
                        mRegisterViewModel.setMaskedImage(new PalmMaskedImage(imageBitmap, Constant.CWidth, Constant.CHeight));
                    }
                });
            }
        }, 10 * 1000); //10000 ms for 10s timeout
        registrationThread.start();
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