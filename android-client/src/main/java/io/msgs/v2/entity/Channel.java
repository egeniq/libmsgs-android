package io.msgs.v2.entity;

import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Channel entity.
 */
public class Channel extends AbstractEntity {

    private final static SimpleDateFormat DATE_FORMAT;
    private static final String TAG = Channel.class.getName();

    static {
        // Interpret dates delivered by the API as en_US in the UTC time zone.
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Constructor.
     */
    public Channel() {
        super();
    }

    /**
     * Constructor.
     */
    public Channel(JSONObject data) {
        super(data);
    }

    /**
     * Get code.
     */
    public String getCode() {
        return _getString("code");
    }

    /**
     * Set code.
     */
    public Channel setCode(String code) {
        _putString("code", code);
        return this;
    }

    /**
     * Get name.
     */
    public String getName() {
        return _getString("name");
    }

    /**
     * Set name.
     */
    public Channel setName(String name) {
        _putString("name", name);
        return this;
    }

    /**
     * Get date when channel was created.
     */
    public Date getCreatedAt() {
        String createdAtString = _getString("createdAt");
        if (createdAtString == null) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(createdAtString);
        } catch (ParseException e) {
            Log.e(TAG, "Can't parse 'createdAt' date: " + createdAtString);
        }
        return null;
    }

    /**
     * Set date when channel was created.
     */
    public Channel setCreatedAt(String createdAt) {
        _putString("createdAt", createdAt);
        return this;
    }

    /**
     * Get date when channel was updated last time.
     */
    public Date getUpdatedAt() {
        String createdAtString = _getString("updatedAt");
        if (createdAtString == null) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(createdAtString);
        } catch (ParseException e) {
            Log.e(TAG, "Can't parse 'updatedAt' date: " + createdAtString);
        }
        return null;
    }

    /**
     * Set date when channel was updated last time.
     */
    public Channel setUpdatedAt(String updatedAt) {
        _putString("updatedAt", updatedAt);
        return this;
    }

    /**
     * Get tags.
     */
    public String[] getTags() {
        return _getStringArray("tags");
    }

    /**
     * Set tags.
     */
    public Channel setTags(String[] tags) {
        _putStringArray("tags", tags);
        return this;
    }

    /**
     * Get data.
     */
    public JSONObject getData() {
        return _getObject("data");
    }

    /**
     * Set data.
     */
    public Channel setData(JSONObject data) {
        _putObject("data", data);
        return this;
    }
}
