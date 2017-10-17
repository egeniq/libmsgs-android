package io.msgs.v2.utils;

import android.os.Build;

public class Utils {
    /**
     * Get the device name, e.g. "Samsung Galaxy S4"
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }
}
