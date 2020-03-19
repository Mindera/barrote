package com.mindera.skeletoid.generic;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.VisibleForTesting;

/**
 * Utility class for Android related info
 */
public class AndroidUtils {

    protected static final String APP_NAME = "App";

    /**
     * Cached app version name
     */
    private static String mAppVersionName = null;

    /**
     * Cached app version code
     */
    protected static int mAppVersionCode = -1;

    /**
     * Cached app package
     */
    protected static String mAppPackage = null;

    @VisibleForTesting
    AndroidUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the device's manufacturer and model name
     *
     * @return String with the device's manufacturer and model name
     * @deprecated Use DeviceUtils.name instead
     */
    @Deprecated()
    public static String getDeviceName() {
        return DeviceUtils.INSTANCE.getName();
    }

    /**
     * Get the device's manufacturer and model name
     *
     * @return String with the device's manufacturer and model name
     * @deprecated Use DeviceUtils.brand instead
     */
    @Deprecated
    public static String getDeviceBrand() {
        return DeviceUtils.INSTANCE.getBrand();
    }

    /**
     * Get installed OS release name
     *
     * @return String with the installed OS release version
     * @deprecated Use DeviceUtils.osRelease instead
     */
    @Deprecated
    public static String getOSReleaseVersion() {
        return DeviceUtils.INSTANCE.getOsRelease();
    }

    /**
     * Get installed OS SDK version
     *
     * @return String with the installed OS SDK version
     * @deprecated Use DeviceUtils.sdkVersion instead
     */
    @Deprecated
    public static int getOSSDKVersion() {
        return DeviceUtils.INSTANCE.getSdkVersion();
    }

    /**
     * Get Device resolution
     *
     * @param context Application context
     * @return Device resolution
     */
    public static String getDeviceResolution(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return null;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        float density = context.getResources().getDisplayMetrics().densityDpi;

        StringBuilder str = new StringBuilder();

        str.append("Width: " + metrics.widthPixels + " px (" + (metrics.widthPixels / density)
                + "dp)");
        str.append("| Height: " + metrics.heightPixels + " px (" + (metrics.heightPixels / density)
                + "dp)");

        return str.toString();
    }

    /**
     * Method to obtain the versionName on the Manifest in runtime
     *
     * @param context Application context
     * @return The string with the application versionName or null if an exception occurs
     */
    public static String getApplicationVersionName(Context context) {

        if (mAppVersionName == null && context != null) {

            PackageInfo info = null;
            try {
                info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                //This has Log instead of LOG on purpose to avoid infinite loops in error cases of logger startup
                Log.e(AndroidUtils.class.getSimpleName(), "getApplicationVersionName", e);
            }

            mAppVersionName = info != null ? info.versionName : "";
        }

        return mAppVersionName;

    }

    /**
     * Method to obtain the versionCode on the Manifest in runtime
     *
     * @param context Application context
     * @return The int with the application versionCode or -1 if an exception occurs
     */
    public static int getApplicationVersionCode(Context context) {

        if (mAppVersionCode < 0 && context != null) {

            try {
                PackageInfo info = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(),
                                PackageManager.GET_META_DATA);

                mAppVersionCode = info.versionCode;

            } catch (Exception e) {
                //This has Log instead of LOG in purpose to avoid infinite loops on error cases of logger startup
                Log.e(AndroidUtils.class.getSimpleName(), "getApplicationVersionCode", e);
            }
        }

        return mAppVersionCode;
    }

    /**
     * Method to obtain the application package on the Manifest in runtime
     *
     * @param context Application context
     * @return The application package.
     */
    public static String getApplicationPackage(Context context) {

        if (mAppPackage == null && context != null) {

            mAppPackage = context.getApplicationContext().getPackageName();

            if (mAppPackage != null) {
                try {
                    PackageInfo info = context.getPackageManager()
                            .getPackageInfo(context.getPackageName(),
                                    PackageManager.GET_META_DATA);

                    mAppPackage = info.packageName;

                } catch (Exception e) {
                    //This has Log instead of LOG in purpose to avoid infinite loops on error cases of logger startup
                    Log.e(AndroidUtils.class.getSimpleName(), "getApplicationPackage", e);
                }
            }
        }

        return mAppPackage;
    }

    /**
     * Check if a service is running
     *
     * @param context      The context
     * @param serviceClass The class of the service to check
     * @return true if it is, false if not.
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        if (serviceClass == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get app name define in AndroidManifest as label. It will throw
     * android.content.res.Resources$NotFoundException if hardcoded.
     *
     * @param context The app context
     * @return Label of the app, or App.cd
     */
    public static String getApplicationName(Context context) {

        String label = APP_NAME;
        try {
            int stringId = context.getApplicationInfo().labelRes;
            label = context.getString(stringId);
        } catch (Exception e) {
            //This has Log instead of LOG on purpose to avoid infinite loops in error cases of logger startup
            Log.e(AndroidUtils.class.getSimpleName(), "getApplicationName", e);
        }
        return label;
    }

    /**
     * Check if a specific package is installed.
     *
     * @param context       App context
     * @param targetPackage The package to check
     * @return true if it is, false if not
     */
    public static boolean checkIfPackageIsInstalled(Context context, String targetPackage) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isPhoneAvailable(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return telephonyManager != null && telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    /**
     * Get File directory path
     *
     * @param context Context
     * @param path    path inside directory path
     * @return Path needed
     */
    public static String getFileDirPath(Context context, String path) {
        return context.getFilesDir().getPath() + path;
    }

    /**
     * Get Cache directory path
     *
     * @param context              Context
     * @param separatorAndFilename filename
     * @return Path needed
     */
    public static String getCacheDirPath(Context context, String separatorAndFilename) {
        return context.getCacheDir().getPath() + separatorAndFilename;
    }


    /**
     * Get External Storage directory path
     *
     * @param separatorAndFilename filename
     * @return Path needed
     */
    public static String getExternalPublicDirectory(String separatorAndFilename) {
        return Environment.getExternalStorageDirectory().getPath() + separatorAndFilename;
    }

    /**
     * Check if device running is an emulator ( https://stackoverflow.com/a/55355049/327011 )
     * @return true if emulator
     */
    private boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    @VisibleForTesting
    public static void deinit() {
        mAppVersionName = null;
        mAppVersionCode = -1;
        mAppPackage = null;
    }
}
