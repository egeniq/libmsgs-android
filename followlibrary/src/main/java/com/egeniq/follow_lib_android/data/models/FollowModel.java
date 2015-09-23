package com.egeniq.follow_lib_android.data.models;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.egeniq.follow_lib_android.Utils;
import com.egeniq.follow_lib_android.data.Constants;
import com.egeniq.follow_lib_android.data.entity.FollowEndpoint;
import com.egeniq.follow_lib_android.data.entity.FollowSubscription;
import com.egeniq.utils.api.APIException;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import io.msgs.v2.Client;
import io.msgs.v2.RequestHelper.Sort;
import io.msgs.v2.entity.Endpoint;
import io.msgs.v2.entity.ItemList;
import io.msgs.v2.entity.Subscription;
import io.msgs.v2.entity.User;


/**
 * Follow model.
 */
public enum FollowModel {
    INSTANCE;

    private final static String TAG = FollowModel.class.getName();
    private final static boolean DEBUG = Constants.DEBUG;

    private String EXTERNAL_USER_ID_SEED = "982e1473d9a78ec4bf6f50bacd6b7d24424574d988d85dca87668ee004cd581e";

    public final static String ACTION_USER_CHANGED = TAG + ".USER_CHANGED_ACTION";

    private final static String ENDPOINT_KEY = "endpoint";
    private final static String EMAIL_KEY = "email";
    private final static String USER_KEY = "user";

    private Context _context;
    private Client _client;

    private User _user;
    private String _email;
    private Endpoint _endpoint;

    private HashMap<String, Boolean> _subscriptionStatusCache = new HashMap<String, Boolean>();

    /**
     * Fetch listener.
     */
    public interface OnFetchListener<T> {
        // @formatter:off
        public void onFetchComplete(T item);
        public void onFetchError(String code, String message);
        // @formatter:on
    }

    /**
     * Fetch list listener.
     */
    public interface OnFetchListListener<T> {
        // @formatter:off
        public void onFetchComplete(T[] items, boolean hasMore);
        public void onFetchError(String code, String message);
        // @formatter:on
    }

    /**
     * Fetch status for subscriptions listener.
     */
    public interface onFetchStatusForSubscriptionsListener {
        // @formatter:off
        public void onFetchCached(HashMap<String, Boolean> subscriptionStatus);
        public void onFetchComplete(HashMap<String, Boolean> subscriptionStatus);
        public void onFetchError(String code, String message);
        // @formatter:on
    }

    /**
     * Action listener.
     */
    public interface OnActionListener {
        // @formatter:off
        public void onActionComplete();
        public void onActionError(String code, String message);
        // @formatter:on
    }

    /**
     * Constructor
     *
     * @param client
     * @param utils
     */
    public void init(Context context, String baseUrl, String apiKey) {
        _context = context;
        _client = new Client(baseUrl, apiKey);

        _loadState();
    }

    /**
     * Load state.
     */
    private void _loadState() {
        try {
            SharedPreferences prefs = _context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
            String userJson = prefs.getString(USER_KEY, null);
            _user = userJson != null ? new User(new JSONObject(userJson)) : null;

            _email = prefs.getString(EMAIL_KEY, null);

            String endpointJson = prefs.getString(ENDPOINT_KEY, null);
            _endpoint = endpointJson != null ? new Endpoint(new JSONObject(endpointJson)) : null;
        } catch (JSONException e) {
        }
    }

    /**
     * Save state.
     */
    private void _saveState() {
        Editor editor = _context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putString(USER_KEY, _user != null ? _user.toJSON().toString() : null);
        editor.putString(EMAIL_KEY, _email);
        editor.putString(ENDPOINT_KEY, _endpoint != null ? _endpoint.toJSON().toString() : null);
        editor.commit();
    }

    /**
     * Has user?
     */
    public boolean isUserLinked() {
        return _user != null;
    }

    /**
     * Returns the user e-mail (if known).
     *
     * @return User e-mail.
     */
    public String getUserEmail() {
        return _email;
    }

    /**
     * Sets the user email.
     *
     * @param email User e-mail.
     */
    protected void _setUserEmail(String email) {
        _email = email;
        _saveState();
    }

    /**
     * Returns the user info.
     *
     * @return User info.
     */
    protected User _getUser() {
        return _user;
    }

    /**
     * Set user.
     *
     * @param user
     */
    protected void _setUser(User user) {
        _user = user;
        _saveState();
    }

    /**
     * Returns the endpoint info.
     *
     * @return Endpoint info.
     */
    protected Endpoint _getEndpoint() {
        return _endpoint;
    }

    /**
     * Set endpoint.
     *
     * @param endpoint
     */
    protected void _setEndpoint(Endpoint endpoint) {
        _endpoint = endpoint;
        _saveState();
    }

    // --- Registration

    /**
     * Calculate the SHA1 for the given input string.
     *
     * @param input
     * @return SHA1 for input string.
     */
    private String _sha1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = digest.digest(input.getBytes("UTF-8"));

            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // should never happen
        }
    }

    /**
     * Link RTLid user.
     *
     * @param rtlIdToken
     * @param email
     * @param listener
     * @return Task.
     */
    public AsyncTask<?, ?, ?> linkRTLidUser(String rtlIdToken, String email, OnActionListener listener) {
        String externalUserId = "rtlid:" + _sha1(rtlIdToken + ":" + EXTERNAL_USER_ID_SEED);
        return linkUser(externalUserId, null, email, listener);
    }

    /**
     * Link user with gigya token.
     *
     * @param gigyaToken
     * @param email
     * @param listener
     * @return Task.
     */
    public AsyncTask<?, ?, ?> linkUserWithGigyaToken(String gigyaToken, String email, OnActionListener listener) {
        String externalUserId = "gigya:" + _sha1(gigyaToken + ":" + EXTERNAL_USER_ID_SEED);
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("gigyaId", gigyaToken);
            json.put("data", data);
        } catch (JSONException e) {
            Log.e(TAG, "Error in JSON in gigya call.", e);
        }
        return linkUser(externalUserId, json, email, listener);
    }

    /**
     * Link Facebook user.
     *
     * @param facebookId
     * @param email
     * @param listener
     * @return Task.
     */
    public AsyncTask<?, ?, ?> linkFacebookUser(String facebookId, String email, OnActionListener listener) {
        String externalUserId = "facebook:" + _sha1(facebookId + ":" + EXTERNAL_USER_ID_SEED);
        return linkUser(externalUserId, null, email, listener);
    }

    /**
     * Link user.
     *
     * @param rtlIdToken
     * @param email
     */
    public AsyncTask<?, ?, ?> linkUser(final String externalUserId, final JSONObject data, final String email, final OnActionListener listener) {
        if (_getUser() != null && _getUser().getExternalUserId().equalsIgnoreCase(externalUserId)) {
            if (listener != null) {
                listener.onActionComplete();
            }

            return null;
        } else if (_getUser() != null) {
            unlinkUser();

            if (listener != null) {
                listener.onActionComplete();
            }

            return null;
        }

        AsyncTask<Void, Void, Object> task = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    // register user
                    User user = data == null ? new User() : new User(data);
                    user.setExternalUserId(externalUserId);
                    user = _client.registerUser(user.toJSON());

                    // (re-)register e-mail endpoint
                    {
                        Endpoint endpoint = new Endpoint();
                        endpoint.setType("email");
                        endpoint.setName(email);
                        endpoint.setAddress(email);
                        endpoint.setUserSubscriptionsActive(false);
                        endpoint.setEndpointSubscriptionsActive(false);
                        endpoint = _client.forUser(user.getToken()).registerEndpoint(endpoint.toJSON());

                        // (re-)subscribe to news channel
                        _client.forEndpoint(endpoint.getToken()).subscribe("news");
                    }

                    // (re-)register device endpoint / move device to user
                    Endpoint endpoint = null;
                    if (_getEndpoint() != null) {
                        endpoint = new Endpoint();
                        endpoint.setType(Utils.getDeviceType(_context));
                        endpoint.setName(io.msgs.v2.utils.Utils.getDeviceName());
                        endpoint.setAddress(_getEndpoint().getAddress());
                        endpoint = _client.forUser(user.getToken()).registerEndpoint(endpoint.toJSON());

                        // (re-)subscribe to news channel
                        _client.forEndpoint(endpoint.getToken()).subscribe("news");
                    }

                    return new Object[] {user, endpoint};
                } catch (APIException e) {
                    if (DEBUG) {
                        Log.e(TAG, "Error linking user", e);
                    }

                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (result instanceof Object[]) {
                    Object[] data = (Object[])result;
                    _setUser((User)data[0]);
                    _setUserEmail(email);
                    _setEndpoint((Endpoint)data[1]);

                    LocalBroadcastManager.getInstance(_context).sendBroadcast(new Intent(ACTION_USER_CHANGED));

                    if (listener != null) {
                        listener.onActionComplete();
                    }
                } else {
                    if (listener != null) {
                        APIException e = (APIException)result;
                        listener.onActionError(e.getCode(), e.getMessage());
                    }
                }
            }
        };

        Utils.executeOnThreadPool(task);
        return task;
    }

    /**
     * Register endpoint.
     *
     * @param gcmRegistrationId
     * @param listener
     */
    public AsyncTask<?, ?, ?> linkEndpoint(final String gcmRegistrationId, final OnActionListener listener) {
        AsyncTask<Void, Void, Object> task = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    if (_getEndpoint() != null && _getEndpoint().getAddress().equalsIgnoreCase(gcmRegistrationId)) {
                        // nothing to update
                        return null;
                    }

                    Endpoint endpoint = new Endpoint();
                    endpoint.setType(Utils.getDeviceType(_context));
                    endpoint.setAddress(gcmRegistrationId);
                    endpoint.setName(io.msgs.v2.utils.Utils.getDeviceName());

                    if (_getEndpoint() == null) {
                        if (_getUser() != null) {
                            // register as new endpoint for the currently logged in user
                            endpoint = _client.forUser(_getUser().getToken()).registerEndpoint(endpoint.toJSON());
                        } else {
                            // register as new endpoint
                            endpoint = _client.registerEndpoint(endpoint.toJSON());
                        }

                        // subscribe to news channel
                        _client.forEndpoint(endpoint.getToken()).subscribe("news");
                    } else {
                        // update existing endpoint registration
                        endpoint = _client.forEndpoint(_getEndpoint().getToken()).update(endpoint.toJSON("address", "name"));
                    }

                    return endpoint;
                } catch (APIException e) {
                    if (DEBUG) {
                        Log.e(TAG, "Error linking endpoint", e);
                    }

                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (result == null) {
                    // nothing to update
                    listener.onActionComplete();
                } else if (result instanceof Endpoint) {
                    _setEndpoint((Endpoint)result);
                    listener.onActionComplete();
                } else {
                    APIException e = (APIException)result;
                    listener.onActionError(e.getCode(), e.getMessage());
                }
            }
        };

        task.execute();
        return task;
    }

    /**
     * Unlink user.
     */
    public void unlinkUser() {
        final String userToken = _getUser() != null ? _getUser().getToken() : null;
        final String endpointToken = _getEndpoint() != null ? _getEndpoint().getToken() : null;
        if (userToken == null /*|| endpointToken == null*/) { // TODO enable this check when device linking works
            return;
        }

        _setUser(null);
        _setUserEmail(null);
        clearCache();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    _client.forUser(userToken).forEndpoint(endpointToken).delete();
                } catch (APIException e) {
                }

                return null;
            }
        };

        Utils.executeOnThreadPool(task);

        LocalBroadcastManager.getInstance(_context).sendBroadcast(new Intent(ACTION_USER_CHANGED));
    }

    // --- Manage endpoints

    /**
     * Fetch user endpoints
     *
     * @param section
     * @param offset
     * @param limit
     * @param listener
     * @return
     */
    public AsyncTask<?, ?, ?> fetchEndpoints(final OnFetchListListener<FollowEndpoint> listener) {
        AsyncTask<Void, Void, Object> task = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    if (_getUser() != null) {
                        ItemList<Endpoint> data = _client.forUser(_getUser().getToken()).fetchEndpoints(null, null);
                        FollowEndpoint[] items = new FollowEndpoint[data.getCount()];
                        for (int i = 0; i < data.getCount(); i++) {
                            items[i] = new FollowEndpoint(data.get(i));
                        }

                        return items;
                    } else if (_getEndpoint() != null) {
                        Endpoint data = _client.forEndpoint(_getEndpoint().getToken()).fetch();
                        FollowEndpoint[] items = {new FollowEndpoint(data)};
                        return items;
                    } else {
                        return new FollowEndpoint[0];
                    }
                } catch (APIException e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (result instanceof FollowEndpoint[]) {
                    listener.onFetchComplete((FollowEndpoint[])result, false);
                } else {
                    APIException e = (APIException)result;
                    listener.onFetchError(e.getCode(), e.getMessage());
                }
            }
        };

        Utils.executeOnThreadPool(task);
        return task;
    }

    /**
     * Update endpoint receives breaking news.
     *
     * @param token
     * @param receivesBreakingNews
     */
    public AsyncTask<?, ?, ?> updateEndpointReceivesBreakingNews(final String token, final boolean receivesBreakingNews, final OnActionListener listener) {
        AsyncTask<Void, Void, Object> task = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    _client.forEndpoint(token).update(new Endpoint().setEndpointSubscriptionsActive(receivesBreakingNews).toJSON());
                    return null;
                } catch (APIException e) {
                    if (DEBUG) {
                        Log.e(TAG, "Error updating endpoint", e);
                    }

                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (result == null) {
                    listener.onActionComplete();
                } else {
                    APIException e = (APIException)result;
                    listener.onActionError(e.getCode(), e.getMessage());
                }
            }
        };

        Utils.executeOnThreadPool(task);
        return task;
    }

    /**
     * Update endpoint receives (follow) updates.
     *
     * @param token
     * @param receivesBreakingNews
     */
    public AsyncTask<?, ?, ?> updateEndpointReceivesUpdates(final String token, final boolean receivesUpdates, final OnActionListener listener) {
        AsyncTask<Void, Void, Object> task = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    _client.forEndpoint(token).update(new Endpoint().setUserSubscriptionsActive(receivesUpdates).toJSON());
                    return null;
                } catch (APIException e) {
                    if (DEBUG) {
                        Log.e(TAG, "Error updating endpoint", e);
                    }

                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (result == null) {
                    listener.onActionComplete();
                } else {
                    APIException e = (APIException)result;
                    listener.onActionError(e.getCode(), e.getMessage());
                }
            }
        };

        Utils.executeOnThreadPool(task);
        return task;
    }

    // --- Manage subscriptions

    /**
     * Fetch subscription status for a single subscription.
     *
     * If the user isn't logged in the returned status will be null.
     *
     * @param channelCode
     * @param listener
     */
    public AsyncTask<?, ?, ?> fetchStatusForSubscription(final String channelCode, final OnFetchListener<Boolean> listener) {
        AsyncTask<Void, Void, Object> task = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    if (_getUser() == null) {
                        return null;
                    }

                    Boolean status = _subscriptionStatusCache.get(channelCode);
                    if (status != null) {
                        return status;
                    }

                    return _client.forUser(_getUser().getToken()).fetchSubscription(channelCode) != null;
                } catch (APIException e) {
                    if (e.getCode().equals("subscription_not_found")) {
                        return false;
                    } else {
                        return e;
                    }
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (result instanceof Boolean) {
                    _subscriptionStatusCache.put(channelCode, (Boolean)result);
                    listener.onFetchComplete((Boolean)result);
                } else if (result instanceof APIException) {
                    APIException error = (APIException)result;
                    listener.onFetchError(error.getCode(), error.getMessage());
                } else {
                    listener.onFetchComplete(null);
                }
            }
        };

        Utils.executeOnThreadPool(task);
        return task;
    }

    /**
     * Fetch subscription status for the given subscriptions.
     *
     * If the user isn't logged in the result will be null.
     *
     * The listener might be called multiple times. In the end hasMore will be false.
     *
     * @param channelCode
     * @param listener
     */
    public AsyncTask<?, ?, ?> fetchStatusForSubscriptions(final Set<String> channelCodes, final onFetchStatusForSubscriptionsListener listener) {
        AsyncTask<Void, HashMap<String, Boolean>, Object> task = new AsyncTask<Void, HashMap<String, Boolean>, Object>() {
            @SuppressWarnings("unchecked")
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    if (_getUser() == null) {
                        return null;
                    }

                    HashSet<String> newChannelCodes = new HashSet<String>(channelCodes);

                    HashMap<String, Boolean> subscriptionStatus = new HashMap<String, Boolean>();
                    for (String channelCode : channelCodes) {
                        Boolean status = _subscriptionStatusCache.get(channelCode);
                        if (status != null) {
                            subscriptionStatus.put(channelCode, status);
                        } else {
                            newChannelCodes.add(channelCode);
                        }
                    }

                    if (newChannelCodes.size() == 0) {
                        return subscriptionStatus;
                    } else if (subscriptionStatus.size() > 0) {
                        publishProgress(subscriptionStatus);
                    }

                    ItemList<Subscription> data = _client.forUser(_getUser().getToken()).fetchSubscriptionsForChannels(newChannelCodes, null, null, null);

                    subscriptionStatus = new HashMap<String, Boolean>();
                    for (String channelCode : channelCodes) {
                        Boolean status = _subscriptionStatusCache.get(channelCode);
                        subscriptionStatus.put(channelCode, status == null ? false : status);
                    }

                    for (int i = 0; i < data.getCount(); i++) {
                        subscriptionStatus.put(data.get(i).getChannel().getCode(), true);
                    }

                    return subscriptionStatus;
                } catch (APIException e) {
                    return e;
                }
            }

            @Override
            protected void onProgressUpdate(HashMap<String, Boolean>... subscriptionStatus) {
                listener.onFetchCached(subscriptionStatus[0]);
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onPostExecute(Object result) {
                if (result instanceof HashMap<?, ?> || result == null) {
                    listener.onFetchComplete((HashMap<String, Boolean>)result);
                } else if (result instanceof APIException) {
                    APIException e = (APIException)result;
                    listener.onFetchError(e.getCode(), e.getMessage());
                }
            }
        };

        Utils.executeOnThreadPool(task);
        return task;
    }

    /**
     * Fetch user subscriptions
     *
     * @param offset
     * @param limit
     * @param listener
     */
    public AsyncTask<?, ?, ?> fetchSubscriptions(final int limit, final int offset, final OnFetchListListener<FollowSubscription> listener) {
        AsyncTask<Void, Void, Object> task = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    if (_getUser() == null) {
                        return new Object[] {new FollowSubscription[0], false};
                    }

                    ItemList<Subscription> data = _client.forUser(_getUser().getToken()).fetchSubscriptions(Sort.CHANNEL_UPDATED_AT_DESC, limit, offset);
                    FollowSubscription[] items = new FollowSubscription[data.getCount()];
                    for (int i = 0; i < data.getCount(); i++) {
                        items[i] = new FollowSubscription(data.get(i));
                    }

                    boolean hasMore = offset + data.getCount() < data.getTotal();

                    return new Object[] {items, hasMore};
                } catch (APIException e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (result instanceof Object[]) {
                    Object[] data = (Object[])result;
                    FollowSubscription[] items = (FollowSubscription[])data[0];
                    boolean hasMore = (Boolean)data[1];
                    listener.onFetchComplete(items, hasMore);
                } else {
                    APIException e = (APIException)result;
                    listener.onFetchError(e.getCode(), e.getMessage());
                }
            }
        };

        Utils.executeOnThreadPool(task);
        return task;
    }

    /**
     * Follow channel.
     *
     * @param channelCode
     * @param listener
     */
    public AsyncTask<?, ?, ?> follow(final String channelCode, final OnActionListener listener) {
        AsyncTask<Void, Void, Object> task = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    if (_getUser() == null) {
                        return new APIException();
                    }

                    _client.forUser(_getUser().getToken()).subscribe(channelCode);
                    return null;
                } catch (APIException e) {
                    if (DEBUG) {
                        Log.e(TAG, "Error following channel", e);
                    }

                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (listener == null) {
                    return;
                }

                if (result == null) {
                    listener.onActionComplete();
                } else {
                    APIException e = (APIException)result;
                    listener.onActionError(e.getCode(), e.getMessage());
                }
            }
        };

        Utils.executeOnThreadPool(task);
        return task;
    }

    /**
     * Unfollow channel.
     *
     * @param channelCode
     * @param listener
     */
    public AsyncTask<?, ?, ?> unfollow(final String channelCode, final OnActionListener listener) {
        AsyncTask<Void, Void, Object> task = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    if (_getUser() == null) {
                        return new APIException();
                    }

                    _client.forUser(_getUser().getToken()).unsubscribe(channelCode);
                    return null;
                } catch (APIException e) {
                    if (DEBUG) {
                        Log.e(TAG, "Error following channel", e);
                    }

                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                if (listener == null) {
                    return;
                }

                if (result == null) {
                    listener.onActionComplete();
                } else {
                    APIException e = (APIException)result;
                    listener.onActionError(e.getCode(), e.getMessage());
                }
            }
        };

        Utils.executeOnThreadPool(task);
        return task;
    }

    /**
     * Clear subscription status cache.
     */
    public void clearCache() {
        _subscriptionStatusCache.clear();
    }
}