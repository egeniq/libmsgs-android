package io.msgs.v2;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

import io.msgs.common.APIException;
import io.msgs.common.client.Client;
import io.msgs.common.log.Logger;
import io.msgs.v2.entity.Endpoint;
import io.msgs.v2.utils.Utils;

/**
 * Simple Msgs.io client which only supports subscriptions for the current device.
 */
public class SimpleMsgsClient {
    private final static String TAG = SimpleMsgsClient.class.getName();

    private final static String ENDPOINT_KEY = "endpoint";

    private final Context _context;
    private final MsgsClient _msgsClient;
    private final String _deviceType;
    private Endpoint _endpoint;

    /**
     * Constructor. Use the {@link Builder} to create a new instance of this class.
     *
     * @param builder The builder to create the instance from.
     */
    private SimpleMsgsClient(Builder builder) {
        _context = builder._context;
        _msgsClient = new MsgsClient.Builder(builder._baseUrl, builder._apiKey)
                .setClient(builder._client)
                .build();
        if (builder._deviceType == null) {
            _deviceType = "android";
        } else {
            _deviceType = builder._deviceType;
        }
        _loadState();
    }

    public static class Builder {
        private final Context _context;
        private final String _baseUrl;
        private final String _apiKey;
        private Client _client;
        private String _deviceType;

        /**
         * Builder constructor with the mandatory parameters.
         *
         * @param context The application or activity context.
         * @param baseUrl Base URL to the msgs.io server.
         * @param apiKey  The API key to authenticate the client with.
         */
        public Builder(Context context, String baseUrl, String apiKey) {
            _context = context;
            _baseUrl = baseUrl;
            _apiKey = apiKey;
        }

        /**
         * Device type. If not set, it will default to 'android'
         *
         * @param deviceType Device type to send to server.
         * @return This Builder instance.
         */
        public Builder setDeviceType(String deviceType) {
            _deviceType = deviceType;
            return this;
        }

        /**
         * Underlying client which will execute the calls.
         *
         * @param client The client which executes the network calls.
         * @return This Builder instance.
         */
        public Builder setClient(Client client) {
            _client = client;
            return this;
        }

        /**
         * Creates a new {@link SimpleMsgsClient}.
         *
         * @return A new {@link SimpleMsgsClient} instace.
         */
        public SimpleMsgsClient build() {
            return new SimpleMsgsClient(this);
        }
    }

    public void setLogLevel(Logger.Level level) {
        _msgsClient.setLogLevel(level);
    }

    public void setLoggingEnabled(boolean enabled) {
        _msgsClient.setLoggingEnabled(enabled);
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
            _endpoint = _msgsClient.forEndpoint(_endpoint.getToken()).update(endpoint.toJSON());
        } else {
            _endpoint = _msgsClient.registerEndpoint(endpoint.toJSON());
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