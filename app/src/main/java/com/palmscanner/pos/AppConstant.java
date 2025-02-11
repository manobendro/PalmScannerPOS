package com.palmscanner.pos;

import android.os.Environment;

import java.io.File;

public class AppConstant {

    //TODO: change when running on real device
    public static boolean UI_TESTING = true;
    public static boolean USE_NFC = true;
    public static String API_DOMAIN = "https://posbackend-x0yp.onrender.com";
    public static final int PALM_REGISTER=0x01;
    public static final int PALM_REGISTER_TIME = 3;
    public static final int PALM_INTENSITY_MAX = 500;
    public static final String DIR_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()
            + (File.separator + "SD_TEMPLATE");
    public static final String HAND_DAT_DIR = DIR_ROOT + File.separator + "HANDS_TEMPLATE";
    public static final String LICENSE_DIR = DIR_ROOT + File.separator + "LICENSE";
    public static final String FIRMWARE_PATH = DIR_ROOT + File.separator + "Firmware";
    public static final String LICENSE_PATH = LICENSE_DIR + File.separator + "license.dat";
    public static final String PALM_IMG_PATH = DIR_ROOT + File.separator + "PALM_IMG";
}
