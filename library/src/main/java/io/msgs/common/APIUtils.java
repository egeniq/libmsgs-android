package io.msgs.common;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Utility class for the API.
 * Created by Daniel Zolnai on 2017-10-17.
 */
public class APIUtils {

    private static final String TAG = APIUtils.class.getName();

    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                // Should not happen since we checked for it.
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static String createQueryString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Device does not support UTF-8?", e);
            }
        }

        return result.toString();
    }
}
