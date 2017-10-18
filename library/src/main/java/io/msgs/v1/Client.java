package io.msgs.v1;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.msgs.common.APIClient;
import io.msgs.common.APIException;
import io.msgs.common.APIUtils;
import io.msgs.common.entity.HttpEntity;
import io.msgs.common.entity.UrlEncodedFormEntity;
import io.msgs.common.log.Logger;
import io.msgs.v1.Subscription.Time;

/**
 * Notification client.
 * <p>
 * All methods are executed synchronously. You are yourself
 * responsible for wrapping the calls in an AsyncTask or something similar.
 */
public class Client {
    private final static String TAG = Client.class.getSimpleName();

    private final static String DEVICE_TOKEN_KEY = "deviceToken";
    private final static String NOTIFICATION_TAG = "NotificationManager";
    private final static String NOTIFICATION_TOKEN_KEY = "notificationToken";
    private final static String LAST_REGISTER_CHANNEL_ID_KEY = "lastRegisterChannelId";
    private final static String UPDATED_AT_KEY = "updatedAt";

    private final static int TOKEN_TIMEOUT_IN_DAYS = 3; // * 24 * 60 * 60; // 3 days
    private final static String DEVICE_FAMILY = "gcm";

    private final static SimpleDateFormat DATE_FORMAT;

    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private final Context _context;
    private final String _serviceBaseURL;
    private final String _appId;

    private APIClient _apiClient;
    private final Logger _logger = new Logger();

    /**
     * Constructor.
     *
     * @param context
     * @param serviceBaseURL
     */
    public Client(Context context, String serviceBaseURL, String appId) {
        _context = context;
        _serviceBaseURL = serviceBaseURL;
        _appId = appId;
        _logger.setTag(Client.class.getName());
    }

    /**
     * Returns the API client.
     *
     * @return API client.
     */
    protected APIClient _getAPIClient() {
        if (_apiClient == null) {
            _apiClient = new APIClient(_serviceBaseURL);
        }

        return _apiClient;
    }

    public void setLogLevel(Logger.Level level) {
        _logger.setLevel(level);
        _apiClient.setLogLevel(level);
    }

    public void setLoggingEnabled(boolean enabled) {
        _logger.setEnabled(enabled);
        _apiClient.setLoggingEnabled(enabled);
    }


    /**
     * Is registered?
     *
     * @return Is registered?
     */
    public boolean isRegistered() {
        return getNotificationToken() != null;
    }

    /**
     * Returns the currently known device token.
     *
     * @return Device token.
     */
    public String getDeviceToken() {
        return _context.getSharedPreferences(NOTIFICATION_TAG, Context.MODE_PRIVATE).getString(_appId + "." + DEVICE_TOKEN_KEY, null);
    }

    /**
     * Returns the current notification token.
     *
     * @return Notification token.
     */
    public String getNotificationToken() {
        return _context.getSharedPreferences(NOTIFICATION_TAG, Context.MODE_PRIVATE).getString(_appId + "." + NOTIFICATION_TOKEN_KEY, null);
    }

    /**
     * Returns the channel identifier used in the last registration request.
     *
     * @return Channel identifier.
     */
    private String _getLastRegisterChannelId() {
        return _context.getSharedPreferences(NOTIFICATION_TAG, Context.MODE_PRIVATE).getString(_appId + "." + LAST_REGISTER_CHANNEL_ID_KEY, null);
    }

    /**
     * Returns the stamp of the last registration data update.
     *
     * @return Registration data update stamp.
     */
    private Date _getUpdatedAt() {
        long updatedAtTime = _context.getSharedPreferences(NOTIFICATION_TAG, Context.MODE_PRIVATE).getLong(_appId + "." + UPDATED_AT_KEY, 0);
        if (updatedAtTime == 0) {
            return null;
        } else {
            return new Date(updatedAtTime);
        }
    }

    /**
     * Register device.
     *
     * @param deviceToken
     */
    public void registerDevice(final String deviceToken) throws APIException {
        registerDevice(deviceToken, null);
    }

    /**
     * Register device and subscribe to the given channel.
     *
     * @param deviceToken
     * @param channelId
     */
    public void registerDevice(final String deviceToken, final String channelId) throws APIException {
        try {
            _logger.d("Send device registration request for device token: " + deviceToken + " app ID: " + _appId);

            // Update needed?
            if (deviceToken.equals(getDeviceToken()) && getNotificationToken() != null) {
                // @formatter:off
                if (((_getLastRegisterChannelId() == null && channelId == null) ||
                        (_getLastRegisterChannelId() != null && _getLastRegisterChannelId().equals(channelId))) &&
                        (_getUpdatedAt() != null && new Date().getTime() - _getUpdatedAt().getTime() < TOKEN_TIMEOUT_IN_DAYS)) {
                    _logger.d("Registration request cancelled, all data seems up-to-date and recent enough");
                    return;
                }
                // @formatter:on
            }

            Map<String, String> params = new HashMap<>();
            params.put("appId", Uri.encode(_appId));
            params.put("deviceFamily", DEVICE_FAMILY);
            params.put("deviceToken", deviceToken);

            if (channelId != null) {
                params.put("channelId", channelId);
            }

            String path = "subscribers";
            String notificationToken = getNotificationToken();
            if (notificationToken != null) {
                _logger.d("Using existing notification token: " + notificationToken);

                path = "subscribers/;update";
                params.put("notificationToken", notificationToken);
            }

            HttpEntity entity = new UrlEncodedFormEntity(params);
            JSONObject result = _getAPIClient().post(path, entity);

            _logger.d("Registration request sent");

            if (result != null) {
                notificationToken = APIUtils.getString(result, "notificationToken", null);
                _saveRegistrationData(deviceToken, notificationToken, channelId);

                _logger.d("Notification token: " + notificationToken);
            } else {
                _logger.d("No notification token returned");
            }

            _logger.d("Registration request processed");
        } catch (Exception e) {
            _logger.e("Error registering device", e);

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Returns a list of subscriptions for this device.
     *
     * @return Subscriptions.
     * @throws APIException
     */
    public Subscription[] getSubscriptions() throws APIException {
        try {
            String notificationToken = getNotificationToken();
            if (notificationToken == null) {
                throw new APIException("Device is not registered");
            }

            JSONArray rawSubscriptions = _getAPIClient().getArray("subscriptions/" + _appId + "/" + notificationToken);

            ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();
            for (int i = 0; i < rawSubscriptions.length(); i++) {
                JSONObject rawSubscription = rawSubscriptions.getJSONObject(i);

                Subscription subscription = new Subscription();
                subscription.setId(APIUtils.getString(rawSubscription, "id", null));
                subscription.setChannelId(APIUtils.getString(rawSubscription, "channelId", ""));

                String rawStartDate = APIUtils.getString(rawSubscription, "dateStart", null);
                Date startDate = rawStartDate == null ? null : DATE_FORMAT.parse(rawStartDate);
                String rawEndDate = APIUtils.getString(rawSubscription, "dateEnd", null);
                Date endDate = rawEndDate == null ? null : DATE_FORMAT.parse(rawEndDate);
                subscription.setDatePeriod(startDate, endDate);

                String rawStartTime = APIUtils.getString(rawSubscription, "timeStart", null);
                Time startTime = rawStartTime == null ? null : new Time(Integer.parseInt(rawStartTime.split(":")[0]), Integer.parseInt(rawStartTime.split(":")[1]));
                String rawEndTime = APIUtils.getString(rawSubscription, "timeEnd", null);
                Time endTime = rawEndTime == null ? null : new Time(Integer.parseInt(rawEndTime.split(":")[0]), Integer.parseInt(rawEndTime.split(":")[1]));
                subscription.setTimePeriod(startTime, endTime);

                int weekdays = 0;
                String rawDowSet = APIUtils.getString(rawSubscription, "dowSet", "");
                String[] rawDays = rawDowSet.split(",");
                for (String rawDay : rawDays) {
                    if (rawDay.equals("1")) {
                        weekdays &= Subscription.SUNDAY;
                    } else if (rawDay.equals("2")) {
                        weekdays &= Subscription.MONDAY;
                    } else if (rawDay.equals("3")) {
                        weekdays &= Subscription.TUESDAY;
                    } else if (rawDay.equals("4")) {
                        weekdays &= Subscription.WEDNESDAY;
                    } else if (rawDay.equals("5")) {
                        weekdays &= Subscription.THURSDAY;
                    } else if (rawDay.equals("6")) {
                        weekdays &= Subscription.FRIDAY;
                    } else if (rawDay.equals("7")) {
                        weekdays &= Subscription.SATURDAY;
                    }
                }
                subscription.setWeekdays(weekdays);

                subscriptions.add(subscription);
            }

            return subscriptions.toArray(new Subscription[0]);
        } catch (Exception e) {
            _logger.e("Error adding subscription", e);
            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Subscribe.
     *
     * @param channelId Channel ID.
     * @throws APIException
     */
    public void subscribe(String channelId) throws APIException {
        subscribe(new Subscription().setChannelId(channelId));
    }

    /**
     * Subscribe.
     *
     * @param subscription
     * @throws APIException
     */
    public void subscribe(Subscription subscription) throws APIException {
        try {
            String notificationToken = getNotificationToken();
            if (notificationToken == null) {
                throw new APIException("Device is not registered");
            }

            _logger.d("Add subscription for channel " + subscription.getChannelId());

            Map<String, String> params = new HashMap<>();
            params.put("appId", _appId);
            params.put("notificationToken", notificationToken);
            params.put("channelId", subscription.getChannelId());

            if (subscription.getStartDate() != null) {
                params.put("dateStart", DATE_FORMAT.format(subscription.getStartDate()));
            }

            if (subscription.getEndDate() != null) {
                params.put("dateEnd", DATE_FORMAT.format(subscription.getEndDate()));
            }

            if (subscription.getStartTime() != null) {
                params.put("timeStart", String.format("%02d:%02d", subscription.getStartTime().getHours(), subscription.getStartTime().getMinutes()));
            }

            if (subscription.getEndTime() != null) {
                params.put("timeEnd", String.format("%02d:%02d", subscription.getEndTime().getHours(), subscription.getEndTime().getMinutes()));
            }

            if (subscription.getWeekdays() > 0) {
                String dowSet = "";
                if (subscription.hasWeekday(Subscription.SUNDAY)) {
                    dowSet += (dowSet.length() > 0 ? "," : "") + "1";
                }
                if (subscription.hasWeekday(Subscription.MONDAY)) {
                    dowSet += (dowSet.length() > 0 ? "," : "") + "2";
                }
                if (subscription.hasWeekday(Subscription.TUESDAY)) {
                    dowSet += (dowSet.length() > 0 ? "," : "") + "3";
                }
                if (subscription.hasWeekday(Subscription.WEDNESDAY)) {
                    dowSet += (dowSet.length() > 0 ? "," : "") + "4";
                }
                if (subscription.hasWeekday(Subscription.THURSDAY)) {
                    dowSet += (dowSet.length() > 0 ? "," : "") + "4";
                }
                if (subscription.hasWeekday(Subscription.FRIDAY)) {
                    dowSet += (dowSet.length() > 0 ? "," : "") + "6";
                }
                if (subscription.hasWeekday(Subscription.SATURDAY)) {
                    dowSet += (dowSet.length() > 0 ? "," : "") + "7";
                }

                params.put("dowSet", dowSet);
            }

            HttpEntity entity = new UrlEncodedFormEntity(params);
            JSONObject result = _getAPIClient().post("subscriptions", entity);
            String id = APIUtils.getString(result, "id", null);
            subscription.setId(id);

            _logger.d("Subscription ID: " + id);
        } catch (Exception e) {
            _logger.e("Error adding subscription", e);

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Unsubscribe from all subscriptions for the given channel.
     *
     * @param channelId Channel ID.
     */
    public void unsubscribe(String channelId) throws APIException {
        String notificationToken = getNotificationToken();
        if (notificationToken == null) {
            throw new APIException("Device is not registered");
        }

        try {
            Map<String, String> params = new HashMap<>(3);
            params.put("appId", _appId);
            params.put("notificationToken", notificationToken);
            params.put("channelId", channelId);

            HttpEntity entity = new UrlEncodedFormEntity(params);
            _getAPIClient().post("subscriptions/;delete", entity);
        } catch (Exception e) {
            _logger.e("Error adding subscription", e);

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Unsubscribe using the given subscription ID.
     *
     * @param subscriptionId Subscription ID.
     */
    public void unsubscribe(int subscriptionId) throws APIException {
        String notificationToken = getNotificationToken();
        if (notificationToken == null) {
            throw new APIException("Device is not registered");
        }

        try {
            Map<String, String> params = new HashMap<>(3);
            params.put("appId", _appId);
            params.put("notificationToken", notificationToken);
            params.put("subscriptionId", String.valueOf(subscriptionId));

            HttpEntity entity = new UrlEncodedFormEntity(params);
            _getAPIClient().post("subscriptions/;delete", entity);
        } catch (Exception e) {
            _logger.e("Error adding subscription", e);

            if (!(e instanceof APIException)) {
                e = new APIException(e);
            }

            throw (APIException)e;
        }
    }

    /**
     * Unsubscribe the given subscription (used the subscription ID).
     *
     * @param subscription Subscription ID.
     */
    public void unsubscribe(Subscription subscription) throws APIException {
        if (subscription.getId() != null) {
            unsubscribe(subscription.getId());
        }
    }

    /**
     * Saves the registration data.
     */
    private void _saveRegistrationData(String deviceToken, String notificationToken, String channelId) {
        SharedPreferences.Editor editor = _context.getSharedPreferences(NOTIFICATION_TAG, Context.MODE_PRIVATE).edit();
        editor.putString(_appId + "." + DEVICE_TOKEN_KEY, deviceToken);
        editor.putString(_appId + "." + NOTIFICATION_TOKEN_KEY, notificationToken);
        editor.putString(_appId + "." + LAST_REGISTER_CHANNEL_ID_KEY, channelId);
        editor.putLong(_appId + "." + UPDATED_AT_KEY, new Date().getTime());
        editor.commit();
    }
}
