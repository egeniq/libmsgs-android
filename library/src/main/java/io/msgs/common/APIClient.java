package io.msgs.common;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import io.msgs.common.entity.HttpEntity;
import io.msgs.common.log.Logger;

/**
 * API client which talks to the msgs.io API.
 * <p>
 * Created by Daniel Zolnai on 2017-10-17.
 */
public class APIClient {

    private final String _baseURL;
    private final Logger _logger = new Logger();

    public APIClient(String baseURL) {
        _baseURL = baseURL;
    }

    public JSONObject post(String path, HttpEntity entity) throws APIException {
        return post(path, entity, null);
    }

    public JSONObject post(String path, HttpEntity entity, Map<String, String> headers) throws APIException {
        // TODO
        return null;
    }

    public JSONObject get(String path, Map<String, String> headers) throws APIException {
        // TODO
        return null;
    }

    public JSONArray getArray(String path) throws APIException {
        // TODO
        return null;
    }

    public JSONObject delete(String path, Map<String, String> headers) {
        // TODO
        return null;
    }


    protected void _setLoggingEnabled(boolean enabled) {
        _logger.setEnabled(enabled);
    }

    protected void _setLogLevel(Logger.Level level) {
        _logger.setLevel(level);
    }

    protected void _setLoggingTag(String tag) {
        _logger.setTag(tag);
    }
}
