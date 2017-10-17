package io.msgs.v2;

import android.util.Log;

import org.json.JSONObject;

import io.msgs.BuildConfig;
import io.msgs.common.APIException;
import io.msgs.v2.entity.Endpoint;

/**
 * Request Helper for Endpoint.
 * 
 */
public class EndpointRequestHelper extends RequestHelper {
    private final static String TAG = EndpointRequestHelper.class.getSimpleName();
    private final static boolean DEBUG = BuildConfig.DEBUG;

    /**
     * Constructor
     */
    public EndpointRequestHelper(Client client, String endpointToken) {
        super(client, "endpoints/" + endpointToken);
    }

    /**
     * Constructor
     */
    public EndpointRequestHelper(RequestHelper parent, String endpointToken) {
        super(parent, "endpoints/" + endpointToken);
    }

    /**
     * Fetch endpoint.
     */
    public Endpoint fetch() throws APIException {
        try {
            JSONObject object = _get(null, null);
            return new Endpoint(object);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error fetching endpoint", e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Update endpoint.
     */
    public Endpoint update(JSONObject data) throws APIException {
        try {
            JSONObject object = _post(null, _getParams(data));
            return new Endpoint(object);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error updating endpoint", e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Delete endpoint.
     */
    public void delete() throws APIException {
        try {
            _delete(null);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error deleting endpoint", e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }
}