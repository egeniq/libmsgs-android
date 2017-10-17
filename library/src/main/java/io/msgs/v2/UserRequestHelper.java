package io.msgs.v2;

import android.util.Log;

import com.egeniq.BuildConfig;
import com.egeniq.utils.api.APIException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
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
     * @param properties
     * 
     * @return Endpoint.
     * 
     * @throws APIException
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
     * @param sort Optional. Pass <b>null</b> to use default value.
     * @param limit Optional. Pass <b>null</b> to use default value.
     */
    public ItemList<Endpoint> fetchEndpoints(Integer limit, Integer offset) throws APIException {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            if (limit != null) {
                params.add(new BasicNameValuePair("limit", String.valueOf(limit)));
            }

            if (offset != null) {
                params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
            }

            JSONObject object = _get("endpoints", params);
            return new ItemList<Endpoint>(Endpoint.class, object);
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
