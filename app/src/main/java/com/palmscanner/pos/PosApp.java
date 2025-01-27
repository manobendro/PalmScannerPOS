package com.palmscanner.pos;

import android.app.Application;
import android.util.Log;

import com.saintdeem.palmvein.usb.utils.PalmUSBManager;
import com.saintdeem.palmvein.usb.utils.PalmUSBManagerListener;

public class PosApp extends Application {
    private PalmUSBManagerListener palmUSBManagerListener;
    private PalmUSBManager palmUSBManager;
    private boolean hasPermission = false;
    public static String TAG = "__App__";

    @Override
    public void onCreate() {
        super.onCreate();

        handlerUsbPermission();
    }

    public void setPalmUSBManagerListener(PalmUSBManagerListener palmUSBManagerListener) {
        this.palmUSBManagerListener = palmUSBManagerListener;
    }

    private void handlerUsbPermission() {
        palmUSBManager = new PalmUSBManager(this, new PalmUSBManagerListener() {
            @Override
            public void onCheckPermission(int result) {
                Log.d(TAG, "onCheckPermission");
                hasPermission = true;
                if (null != palmUSBManagerListener) {
                    palmUSBManagerListener.onCheckPermission(result);
                }
            }

            @Override
            public void onUSBArrived() {
                Log.d(TAG, "onUSBArrived");
                if (null != palmUSBManagerListener) {
                    palmUSBManagerListener.onUSBArrived();
                }
            }

            @Override
            public void onUSBRemoved() {
                Log.d(TAG, "onUSBRemoved");
                hasPermission = false;
                if (null != palmUSBManagerListener) {
                    palmUSBManagerListener.onUSBRemoved();
                }
            }
        });
    }

    public void initUSBPermission() {
        palmUSBManager.initUSBPermission();
    }

    public boolean hasPermission() {
        return hasPermission;
    }
}
