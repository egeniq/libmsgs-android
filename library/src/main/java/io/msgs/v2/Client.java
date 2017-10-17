package io.msgs.v2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.msgs.BuildConfig;
import io.msgs.common.APIException;
import io.msgs.common.APIUtils;
import io.msgs.common.entity.UrlEncodedFormEntity;
import io.msgs.v2.entity.Endpoint;
import io.msgs.v2.entity.User;

/**
 * Msgs client.
 * <p>
 * All methods are executed synchronously. You are responsible for <br>
 * wrapping the calls in an AsyncTask or something similar.
 */
public class Client {
    private final static String TAG = Client.class.getSimpleName();
    private final static boolean DEBUG = BuildConfig.DEBUG;

    private final String _baseURL;
    private final String _apiKey;

    private APIClient _apiClient;

    private static class APIClient extends io.msgs.common.APIClient {
        public APIClient(String baseURL) {
            super(baseURL);
            _setLoggingEnabled(DEBUG);
            _setLoggingTag(getClass().getName());
        }
    }


    /**
     * Constructor.
     *
     * @param baseURL The API base URL.
     * @param apiKey  The API key to register with.
     */
    public Client(String baseURL, String apiKey) {
        _baseURL = baseURL;
        _apiKey = apiKey;
    }

    /**
     * Returns the API client.
     *
     * @return API client.
     */
    protected APIClient _getAPIClient() {
        if (_apiClient == null) {
            _apiClient = new APIClient(_baseURL);
        }

        return _apiClient;
    }

    /**
     * Register endpoint.
     *
     * @param data Additional info to send.
     * @return Endpoint The registered endpoint
     * @throws APIException Thrown if there was an error while registering.
     */
    public Endpoint registerEndpoint(JSONObject data) throws APIException {
        try {
            JSONObject object = _post("endpoints", _getParams(data));
            return new Endpoint(object);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error registering endpoint", e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Register user.
     *
     * @param data Additional data to send about the user.
     * @return User The registered user object.
     * @throws APIException Thrown if there was an error while registering the user.
     */
    public User registerUser(JSONObject data) throws APIException {
        try {
            JSONObject object = _post("users", _getParams(data));
            return new User(object);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error registering user", e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * User helper.
     *
     * @param token User token.
     */
    public UserRequestHelper forUser(String token) {
        return new UserRequestHelper(this, token);
    }

    /**
     * Endpoint helper.
     *
     * @param token Endpint token.
     */
    public EndpointRequestHelper forEndpoint(String token) {
        return new EndpointRequestHelper(this, token);
    }

    /**
     * Get Api Header
     */
    private Map<String, String> _getApiHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-MsgsIo-APIKey", _apiKey);
        return headers;
    }

    /**
     * Convert JSON object to name value pairs.
     *
     * @param data Extra parameters to send.
     * @return Name value pairs.
     */
    protected Map<String, String> _getParams(JSONObject data) {
        Map<String, String> params = new HashMap<>();

        Iterator<?> iter = data.keys();
        while (iter.hasNext()) {
            try {
                String key = (String)iter.next();
                Object value = data.get(key);
                if (value != null) {
                    if (value instanceof Boolean) {
                        value = (Boolean)value ? 1 : 0;
                    }

                    params.put(key, String.valueOf(value));
                }
            } catch (JSONException e) {
            }
        }

        return params;
    }

    /**
     * Perform a GET request with the ApiKey header.
     */
    protected JSONObject _get(String path, Map<String, String> params) throws APIException {
        return _getAPIClient().get(path + (params != null && !params.isEmpty() ? "?" + APIUtils.createQueryString(params) : ""), _getApiHeader());
    }

    /**
     * Perform a POST request with the ApiKey header.
     */
    protected JSONObject _post(String path, Map<String, String> params) throws APIException {
        return _getAPIClient().post(path, params == null ? null : new UrlEncodedFormEntity(params), _getApiHeader());
    }

    /**
     * Perform a DELETE request with the ApiKey header.
     */
    protected JSONObject _delete(String path) throws APIException {
        return _getAPIClient().delete(path, _getApiHeader());
    }
}
