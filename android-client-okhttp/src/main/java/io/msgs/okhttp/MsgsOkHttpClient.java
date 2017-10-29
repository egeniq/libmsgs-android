package io.msgs.okhttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import io.msgs.common.APIException;
import io.msgs.common.client.Client;
import io.msgs.common.entity.HttpEntity;
import io.msgs.common.log.Logger;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Msgs.io client which wraps around an OkHttp client.
 * Created by Daniel Zolnai on 2017-10-18.
 */
public class MsgsOkHttpClient implements Client {

    private final OkHttpClient _okHttpClient;
    private final Logger _logger = new Logger();

    public MsgsOkHttpClient(OkHttpClient okHttpClient) {
        _okHttpClient = okHttpClient;
        _logger.setTag(MsgsOkHttpClient.class.getName());
    }

    @Override
    public JSONObject post(String url, HttpEntity entity, Map<String, String> headers) throws APIException {
        _logger.d("--> POST " + url);
        String requestBody;
        try {
            requestBody = entity.getBody();
        } catch (IOException ex) {
            throw new APIException(ex);
        }
        Headers callHeaders = _mapToHeaders(headers);
        Request request = new Request.Builder()
                .url(url)
                .headers(callHeaders)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .build();
        try {
            Response response = _okHttpClient.newCall(request).execute();
            _logger.d("<-- POST " + url + " [" + response.code() + "]");
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String bodyString = responseBody.string();
            _logger.d(bodyString);
            return new JSONObject(bodyString);
        } catch (IOException ex) {
            throw new APIException(ex);
        } catch (JSONException ex) {
            throw new APIException(ex);
        }
    }

    @Override
    public JSONObject get(String url, Map<String, String> headers) throws APIException {
        _logger.d("--> GET " + url);
        Headers callHeaders = _mapToHeaders(headers);
        Request request = new Request.Builder()
                .url(url)
                .headers(callHeaders)
                .get()
                .build();
        try {
            Response response = _okHttpClient.newCall(request).execute();
            _logger.d("<-- GET " + url + " [" + response.code() + "]");
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String bodyString = responseBody.string();
            _logger.d(bodyString);
            return new JSONObject(bodyString);
        } catch (IOException ex) {
            throw new APIException(ex);
        } catch (JSONException ex) {
            throw new APIException(ex);
        }
    }

    @Override
    public JSONArray getArray(String url, Map<String, String> headers) throws APIException {
        _logger.d("--> GET " + url);
        Headers callHeaders = _mapToHeaders(headers);
        Request request = new Request.Builder()
                .url(url)
                .headers(callHeaders)
                .get()
                .build();
        try {
            Response response = _okHttpClient.newCall(request).execute();
            _logger.d("<-- GET " + url + " [" + response.code() + "]");
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String bodyString = responseBody.string();
            _logger.d(bodyString);
            return new JSONArray(bodyString);
        } catch (IOException ex) {
            throw new APIException(ex);
        } catch (JSONException ex) {
            throw new APIException(ex);
        }
    }

    @Override
    public JSONObject delete(String url, Map<String, String> headers) throws APIException {
        _logger.d("--> DELETE " + url);
        Headers callHeaders = _mapToHeaders(headers);
        Request request = new Request.Builder()
                .url(url)
                .headers(callHeaders)
                .delete()
                .build();
        try {
            Response response = _okHttpClient.newCall(request).execute();
            _logger.d("<-- DELETE " + url + " [" + response.code() + "]");
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            String bodyString = responseBody.string();
            _logger.d(bodyString);
            return new JSONObject(bodyString);
        } catch (IOException ex) {
            throw new APIException(ex);
        } catch (JSONException ex) {
            throw new APIException(ex);
        }
    }

    @Override
    public void setLoggingEnabled(boolean enabled) {

    }

    @Override
    public void setLogLevel(Logger.Level level) {

    }

    private Headers _mapToHeaders(Map<String, String> headersMap) {
        Headers.Builder callHeadersBuilder = new Headers.Builder();
        if (headersMap != null) {
            for (Map.Entry<String, String> header : headersMap.entrySet()) {
                callHeadersBuilder.add(header.getKey(), header.getValue());
            }
        }
        return callHeadersBuilder.build();
    }
}
