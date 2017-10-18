package io.msgs.v2;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

import io.msgs.common.APIException;
import io.msgs.v2.entity.Endpoint;
import io.msgs.v2.utils.Utils;

/**
 * Simple Msgs.io client which only supports subscriptions for the current device.
 */
public class SimpleClient {
    private final static String TAG = SimpleClient.class.getName();

    private final static String ENDPOINT_KEY = "endpoint";

    private final Context _context;
    private final Client _client;
    private final String _deviceType;
    private Endpoint _endpoint;

    /**
     * Constructor.
     * 
     * @param baseURL
     * @param apiKey
     */
    public SimpleClient(Context context, String baseURL, String apiKey) {
        this(context, baseURL, apiKey, "android");
    }

    /**
     * Constructor.
     * 
     * @param context
     * @param baseURL
     * @param apiKey
     * @param deviceType
     */
    public SimpleClient(Context context, String baseURL, String apiKey, String deviceType) {
        _context = context;
        _client = new Client(baseURL, apiKey);
        _deviceType = deviceType;
        _loadState();
    }

    /**
     * Load state.
     */
    private void _loadState() {
        try {
            String json = _context.getSharedPreferences(TAG, Context.MODE_PRIVATE).getString(ENDPOINT_KEY, null);
            _endpoint = json != null ? new Endpoint(new JSONObject(json)) : null;
        } catch (JSONException e) {
        }
    }

    /**
     * Save state.
     */
    private void _saveState() {
        Editor editor = _context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putString(ENDPOINT_KEY, _endpoint != null ? _endpoint.toJSON().toString() : null);
        editor.commit();
    }

    /**
     * Register device.
     * 
     * @param registrationId GCM registration id.
     * 
     * @throws APIException
     */
    public Endpoint registerEndpoint(String registrationId) throws APIException {
        if (_endpoint != null && registrationId.equals(_endpoint.getAddress())) {
            return _endpoint;
        }

        Endpoint endpoint = new Endpoint();
        endpoint.setType(_deviceType);
        endpoint.setAddress(registrationId);
        endpoint.setName(Utils.getDeviceName());

        if (_endpoint != null) {
            _endpoint = _client.forEndpoint(_endpoint.getToken()).update(endpoint.toJSON());
        } else {
            _endpoint = _client.registerEndpoint(endpoint.toJSON());
        }

        _saveState();

        return _endpoint;
    }

    /**
     * Returns the currently registered endpoint.
     * 
     * @return Currently registered endpoint.
     */
    public Endpoint getEndpoint() {
        return _endpoint;
    }

    /**
     * Unregister device.
     */
    public void unregisterEndpoint() {
        _endpoint = null;
        _saveState();
    }
}