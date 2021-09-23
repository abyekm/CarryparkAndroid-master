package net.simplifiedcoding.carrypark;

        import android.app.Activity;
        import android.content.Context;
        import android.content.pm.PackageManager;
        import androidx.core.app.ActivityCompat;

public class RuntimePermissionUtil {

    private RuntimePermissionUtil() {

    }



    public static void requestPermission(final Activity activity, final String[] permissions,
                                         final int REQUEST_CODE) {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
    }

    public static void requestPermission(final Activity activity, final String permission,
                                         final int REQUEST_CODE) {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(activity, new String[] { permission }, REQUEST_CODE);
    }

    public static boolean checkPermissonGranted(Context context, String permission) {
        return (
                ActivityCompat.checkSelfPermission(context, permission)
                        ==
                        PackageManager.PERMISSION_GRANTED
        );
    }
}
