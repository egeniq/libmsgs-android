package com.egeniq.msgs.follow;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

/**
 * Utils class
 */
public class Utils {

    /**
     * Get device type based on platform.
     */
    public static String getDeviceType(Context context) {
        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            return "android-tablet";
        } else {
            return "android-phone";
        }
    }


    /**
     * Execute in parallel.
     *
     * @param task
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static <Params> void executeOnThreadPool(AsyncTask<Params, ?, ?> task, Params... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }


}
