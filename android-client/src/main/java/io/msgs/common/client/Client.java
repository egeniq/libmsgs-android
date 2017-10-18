package io.msgs.common.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import io.msgs.common.APIException;
import io.msgs.common.entity.HttpEntity;
import io.msgs.common.log.Logger;

/**
 * Interface for clients to implement.
 * Created by Daniel Zolnai on 2017-10-18.
 */
public interface Client {
    JSONObject post(String url, HttpEntity entity, Map<String, String> headers) throws APIException;

    JSONObject get(String url, Map<String, String> headers) throws APIException;

    JSONArray getArray(String url, Map<String, String> headers) throws APIException;

    JSONObject delete(String url, Map<String, String> headers) throws APIException;

    void setLoggingEnabled(boolean enabled);

    void setLogLevel(Logger.Level level);
}
