package io.vape.imagepicker;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class PermissionUtils {
    public static boolean checkPermissions(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermisions(@NonNull Activity activity, String[] permissions, int code) {
        // Should we show an explanation?
        //if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions)) {
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
        //} else {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(activity, permissions, code);
        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
        //}
    }
}