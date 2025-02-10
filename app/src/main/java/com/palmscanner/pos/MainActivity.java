package com.palmscanner.pos;

import android.Manifest;
import android.app.ComponentCaller;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.palmscanner.pos.database.PosSqliteDB;
import com.palmscanner.pos.database.model.User;
import com.palmscanner.pos.utils.PermissionManager;
import com.saintdeem.palmvein.SDPVUnifiedAPI;
import com.saintdeem.palmvein.device.bean.DeviceMsg;
import com.saintdeem.palmvein.service.bean.ServiceMsg;
import com.saintdeem.palmvein.usb.utils.PalmUSBManagerListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PalmUSBManagerListener, View.OnClickListener {
    private static final int ACTION_REQUEST_PERMISSIONS = 0x02;
    private static final int FILE_REQUEST_PERMISSIONS = 0x03;
    public static String TAG = "---MAIN_ACTIVITY---";
    private final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SDPVUnifiedAPI mSdpvUnifiedAPI;
    private PosSqliteDB posSqliteDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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

        (findViewById(R.id.pay_button)).setOnClickListener(this);
        (findViewById(R.id.register_button)).setOnClickListener(this);
        if (!AppConstant.UI_TESTING) fileManagerPermission();

//        CardReaderDevice.getInstance().initCardReader();

    }

    //----Start PalmUSBManagerListener------
    @Override
    public void onCheckPermission(int result) {
        if (result == 0) {
            Log.d(TAG, "Permission request successful.");
            DeviceMsg<String> msg = mSdpvUnifiedAPI.initDevice(getApplicationContext());
            if (msg.resultCode != 0) {
                Log.d(TAG, "Device initialization results：" + msg.messageTip);
            } else {
                Log.d(TAG, "Device initialization results：" + msg.messageTip);
                // Determine whether the certificate file exists
                File f = new File(AppConstant.LICENSE_PATH);
                if (!f.exists()) {
                    writeLicense();
                }
                getDeviceInfo();
            }
        } else if (result == -1) {
            Log.d(TAG, "Palm vein device not found");
        } else if (result == -2) {
            Log.d(TAG, "Palm vein permission request failed");
        }
    }

    @Override
    public void onUSBArrived() {
        ((PosApp) getApplication()).initUSBPermission();
    }

    @Override
    public void onUSBRemoved() {
        mSdpvUnifiedAPI.terminateDevice();
    }

    //----End PalmUSBManagerListener------


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);
        if (requestCode == FILE_REQUEST_PERMISSIONS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    initData();
                } else {
                    Toast.makeText(this, "Permission not granted.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void fileManagerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Determine whether you have permission, if not, apply for file management
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, FILE_REQUEST_PERMISSIONS);
            } else {
                initData();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            otherManagerPermission();
        } else {
            initData();
        }
    }

    protected void initData() {
        mSdpvUnifiedAPI = SDPVUnifiedAPI.getInstance();
        String sdkVersion = SDPVUnifiedAPI.getSDKVersion();
        Log.d(TAG, "SDPVUnifiedAPI Version: " + sdkVersion);

        posSqliteDB = new PosSqliteDB(this);

        if (!mSdpvUnifiedAPI.hasCacheInit()) {
            mSdpvUnifiedAPI.initCachePool(this, SDPVUnifiedAPI.ChipType.RK3568);
            ArrayList<User> users = posSqliteDB.queryAllUser();

            if (!users.isEmpty()) {
                for (User user : users) {
                    byte[] template = Base64.decode(user.getPalmTemplate(), Base64.DEFAULT);
                    Log.d(TAG, "User template : " + user.getPalmTemplate());
                    Log.d(TAG, "User UUID : " + user.getUuid());
                    try {
                        mSdpvUnifiedAPI.insertPalm(template, user.getUuid());
                    } catch (Exception e) {
                        Log.e(TAG, "Exception while inserting palm template:\n" + e.getMessage());
                    }
                }
            }
            Log.d(TAG, "Palm Count: " + mSdpvUnifiedAPI.getPalmsCount());
        }


        ((PosApp) getApplication()).setPalmUSBManagerListener(this);
        ((PosApp) getApplication()).initUSBPermission();

        createAimDirs();

        File f = new File(AppConstant.LICENSE_PATH);
        if (!f.exists()) {
            writeLicense();
        }

        ServiceMsg<String> initService = mSdpvUnifiedAPI.initService(AppConstant.LICENSE_PATH);
        // Initialize the depth sorting model
        mSdpvUnifiedAPI.initModel();
        Log.d(TAG, "MainActivity InitService: " + initService.messageTip);
    }

    private void writeLicense() {
        InputStream LicenseIs = this.getResources().openRawResource(R.raw.license);
        OutputStream output = null;
        try {
            // Custom file directory, needs to be the same as the initialization algorithm transmission directory
            output = new FileOutputStream(AppConstant.LICENSE_PATH);
            // Copy to output stream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = LicenseIs.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) output.flush();
                if (output != null) output.close();
                LicenseIs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDeviceInfo() {
        if (!mSdpvUnifiedAPI.isDeviceConnect()) {
            mSdpvUnifiedAPI.initDevice(getApplicationContext());
        }
        String deviceVersion = mSdpvUnifiedAPI.getDevice().getVersion();
        Log.d(TAG, "Device Version：" + deviceVersion);
        String ServiceVersion = mSdpvUnifiedAPI.getService().getVersion();
        Log.d(TAG, "Service Version：" + ServiceVersion);
        String serialNumber = mSdpvUnifiedAPI.getSerialNumber().getData();
        Log.d(TAG, "Serial Number：" + serialNumber);
        String firmwareVersion = mSdpvUnifiedAPI.getFirmwareVersion().getData();
        Log.d(TAG, "Firmware Version：" + firmwareVersion);
        String version = SDPVUnifiedAPI.getSDKVersion();
        Log.d(TAG, "SDK Version：" + version);
    }

    protected void otherManagerPermission() {
        if (!PermissionManager.checkPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, ACTION_REQUEST_PERMISSIONS);
        } else {
            initData();
        }
    }

    private void createAimDirs() {
        // Signature file storage location
        createDirPath(AppConstant.HAND_DAT_DIR);
        // Certificate storage directory
        createDirPath(AppConstant.LICENSE_DIR);
        // Palm vein image storage directory
        createDirPath(AppConstant.PALM_IMG_PATH);
        // Device upgrade firmware storage directory
        createDirPath(AppConstant.FIRMWARE_PATH);
    }

    /**
     * Create relevant folders and obtain relevant paths
     */
    private void createDirPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
    }

    @Override
    protected void onDestroy() {

        mSdpvUnifiedAPI.terminateDevice();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pay_button) {

            ImageButton btn = (ImageButton) v;
            btn.setImageResource(R.drawable.paybtn_clicked);
            btn.postDelayed(() -> {
                btn.setImageResource(R.drawable.paybtn_simple);
            }, 20);

            startActivity(new Intent(MainActivity.this, PaymentActivity.class));
        } else if (v.getId() == R.id.register_button) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        }
    }
}