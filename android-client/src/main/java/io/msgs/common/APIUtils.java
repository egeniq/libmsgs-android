package io.msgs.common;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Utility class for the API.
 * Created by Daniel Zolnai on 2017-10-17.
 */
public class APIUtils {

    private static final String TAG = APIUtils.class.getName();

    private final static SimpleDateFormat _dateFormat;

    static {
        // Interpret dates delivered by the API as en_US in the UTC time zone.
        _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        _dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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

    public static Date getDate(JSONObject object, String key, Date fallback) {
        return getDate(object, key, fallback, null);
    }

    public static Date getDate(JSONObject object, String key, Date fallback, SimpleDateFormat format) {
        try {
            return !object.has(key) || object.isNull(key) ? fallback : format == null ? _dateFormat.parse(object.getString(key)) : format.parse(object.getString(key));
        } catch (JSONException e) {
            return fallback;
        } catch (ParseException e) {
            return fallback;
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return _dateFormat.format(date);
    }
}
