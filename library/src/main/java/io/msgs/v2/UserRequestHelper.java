package io.msgs.v2;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.msgs.BuildConfig;
import io.msgs.common.APIException;
import io.msgs.v2.entity.Endpoint;
import io.msgs.v2.entity.ItemList;

/**
 * Request Helper for User.
 */
public class UserRequestHelper extends RequestHelper {
    private final static String TAG = UserRequestHelper.class.getSimpleName();
    private final static boolean DEBUG = BuildConfig.DEBUG;

    /**
     * Constructor
     */
    public UserRequestHelper(Client client, String userToken) {
        super(client, "users/" + userToken);
    }

    /**
     * Register endpoint.
     *
     * @param data Extra info to send with registration.
     * @return Endpoint.
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
     * Get Endpoints.
     *
     * @param limit  Optional. Pass <b>null</b> to use default value.
     * @param offset Optional. Pass <b>null</b> to use default value.
     */
    public ItemList<Endpoint> fetchEndpoints(Integer limit, Integer offset) throws APIException {
        try {
            Map<String, String> params = new HashMap<>(3);

            if (limit != null) {
                params.put("limit", String.valueOf(limit));
            }

            if (offset != null) {
                params.put("offset", String.valueOf(offset));
            }

            JSONObject object = _get("endpoints", params);
            return new ItemList<>(Endpoint.class, object);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error getting endpoints for user", e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Get endpoint helper.
     *
     * @param endpointToken
     */
    public EndpointRequestHelper forEndpoint(String endpointToken) {
        return new EndpointRequestHelper(this, endpointToken);
    }
}
