package com.project.phoenixNotes.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class Constants {
    public static int sHeight;
    public static int sWidth;
    public static final int REQUEST_CODE_FROM_FACE_CHOOSE = 11;

    public static boolean checkPermissionForPhoneState(Context context) {
        if (context != null) {
            int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
