package com.palmscanner.pos;

import android.os.Environment;

import java.io.File;

public class AppConstant {

    //TODO: change when running on real device
    public static boolean UI_TESTING = true;
    public static boolean USE_NFC = true;
    public static final int PALM_REGISTER=0x01;

    // 注册等待时间
    public static final int PALM_REGISTER_TIME = 3;

    // 设备距离提示
    public static final int PALM_INTENSITY_MAX = 500;

    //存储数据的根文件夹路径
    public static final String DIR_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()
            + (File.separator + "SD_TEMPLATE");
    // 手掌特征存储位置
    public static final String HAND_DAT_DIR = DIR_ROOT + File.separator + "HANDS_TEMPLATE";
    //证书所在的文件夹路径
    public static final String LICENSE_DIR = DIR_ROOT + File.separator + "LICENSE";
    //固件存储路径
    public static final String FIRMWARE_PATH = DIR_ROOT + File.separator + "Firmware";
    //证书所在的路径
    public static final String LICENSE_PATH = LICENSE_DIR + File.separator + "license.dat";
    //掌静脉图片存储
    public static final String PALM_IMG_PATH = DIR_ROOT + File.separator + "PALM_IMG";
}
