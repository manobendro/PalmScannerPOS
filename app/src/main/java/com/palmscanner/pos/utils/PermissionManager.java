package com.palmscanner.pos.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class PermissionManager {
    public static boolean checkPermissions(Context mContext, String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(mContext, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }
}
