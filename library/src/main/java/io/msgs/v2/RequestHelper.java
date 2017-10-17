package io.msgs.v2;

import android.text.TextUtils;
import android.util.Log;

import com.egeniq.BuildConfig;
import com.egeniq.utils.api.APIException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import io.msgs.v2.entity.ItemList;
import io.msgs.v2.entity.Subscription;

/**
 * Base RequestHelper
 */
public abstract class RequestHelper {
    private final static String TAG = RequestHelper.class.getSimpleName();
    private final static boolean DEBUG = BuildConfig.DEBUG;

    private Client _client;
    private String _basePath;

    public enum Sort {
        // @formatter:off
        CREATED_AT("createdAt"), 
        CHANNEL_CREATED_AT("channel.createdAt"), 
        CHANNEL_CREATED_AT_ASC("channel.createdAt asc"), 
        CHANNEL_CREATED_AT_DESC("channel.createdAt desc"), 
        CHANNEL_UPDATED_AT("channel.updatedAt"), 
        CHANNEL_UPDATED_AT_ASC("channel.updatedAt asc"), 
        CHANNEL_UPDATED_AT_DESC("channel.updatedAt desc");
        // @formatter:off

        private String _name;

        private Sort(String name) {
            _name = name;
        }

        @Override
        public String toString() {
            return _name;
        }
    }

    /**
     * Constructor.
     * 
     * @param client
     * @param basePath
     */
    public RequestHelper(Client client, String basePath) {
        _client = client;
        _basePath = basePath;
    }
    
    /**
     * Constructor.
     * 
     * @param client
     * @param basePath
     */
    public RequestHelper(RequestHelper parent, String basePath) {
        _client = parent._client;
        _basePath = parent._basePath + "/" + basePath;
    }    

    /**
     * Get subscription.
     * 
     * @param channelCode
     */
    public Subscription fetchSubscription(String channelCode) throws APIException {
        try {
            JSONObject object = _get("subscriptions/" + channelCode, null);
            return new Subscription(object);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, String.format("Error getting subscription on channel '%s' for user or endpoint", channelCode), e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }    
    
    /**
     * Fetch subscriptions.
     * 
     * @param sort   Optional. Pass <b>null</b> to use default value.
     * @param limit  Optional. Pass <b>null</b> to use default value.
     * @param offset Optional. Pass <b>null</b> to use default value.
     */
    public ItemList<Subscription> fetchSubscriptions(Sort sort, Integer limit, Integer offset) throws APIException {
        return _fetchSubscriptions(null, null, sort, limit, offset);
    }
    
    /**
     * Fetch subscriptions.
     * 
     * @param channelCodes Array of channel codes.
     * @param sort         Optional. Pass <b>null</b> to use default value.
     * @param limit        Optional. Pass <b>null</b> to use default value.
     * @param offset       Optional. Pass <b>null</b> to use default value.
     */
    public ItemList<Subscription> fetchSubscriptionsForChannels(Set<String> channelCodes, Sort sort, Integer limit, Integer offset) throws APIException {
        return _fetchSubscriptions(channelCodes, null, sort, limit, offset);        
    }
    
    /**
     * Fetch subscriptions.
     * 
     * @param tags      Array of tags.
     * @param sort      Optional. Pass <b>null</b> to use default value.
     * @param limit     Optional. Pass <b>null</b> to use default value.
     * @param offset    Optional. Pass <b>null</b> to use default value.
     */
    public ItemList<Subscription> fetchSubscriptionsForTags(Set<String> tags, Sort sort, Integer limit, Integer offset) throws APIException {
        return _fetchSubscriptions(null, tags, sort, limit, offset);
    }
    
    /**
     * Fetch subscriptions.
     * 
     * @param channelCode Array of channel codes.
     * @param tags        Array of tags.
     * @param sort        Optional. Pass <b>null</b> to use default value.
     * @param limit       Optional. Pass <b>null</b> to use default value.
     * @param offset      Optional. Pass <b>null</b> to use default value.
     */
    protected ItemList<Subscription> _fetchSubscriptions(Set<String> channelCodes, Set<String> tags, Sort sort, Integer limit, Integer offset) throws APIException {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            if (channelCodes != null) {
                params.add(new BasicNameValuePair("channelCodes", TextUtils.join(",", channelCodes)));
            }
            
            if (tags != null) {
                params.add(new BasicNameValuePair("tags", TextUtils.join(",", tags)));
            }
            
            if (limit != null) {
                params.add(new BasicNameValuePair("limit", String.valueOf(limit)));
            }
            
            if (offset != null) {
                params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
            }
            
            if (sort != null) {
                params.add(new BasicNameValuePair("sort", sort.toString()));
            }

            JSONObject object = _get("subscriptions", params);
            return new ItemList<Subscription>(Subscription.class, object);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error getting subscriptions for user or endpoint", e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Subscribe.
     * 
     * @param channelCode
     */
    public Subscription subscribe(String channelCode) throws APIException {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("channelCode", channelCode));

            JSONObject object = _post("subscriptions", params);
            return new Subscription(object);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error subscribing user or endpoint", e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Unsubscribe.
     * 
     * @param channelCode
     */
    public void unsubscribe(String channelCode) throws APIException {
        try {
            _delete("subscriptions/" + channelCode);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error unsubscribing user or endpoint", e);
            }

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }
    
    /**
     * Perform a GET request with the ApiKey header.
     */
    protected JSONObject _get(String path, List<NameValuePair> params) throws APIException {
        return _client._get(path == null ? _basePath : _basePath + "/" + path, params);
    }

    /**
     * Perform a POST request with the ApiKey header.
     */
    protected JSONObject _post(String path, List<NameValuePair> params) throws APIException {
        return _client._post(path == null ? _basePath : _basePath + "/" + path, params);
    }

    /**
     * Perform a DELETE request with the ApiKey header.
     */
    protected JSONObject _delete(String path) throws APIException {
        return _client._delete(path == null ? _basePath : _basePath + "/" + path);
    }    
    
    /**
     * Convert JSON object to name value pairs.
     * 
     * @param properties
     * 
     * @return Name value pairs.
     */
    protected List<NameValuePair> _getParams(JSONObject data) {
        return _client._getParams(data);
    }    
}