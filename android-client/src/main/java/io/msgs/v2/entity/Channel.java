package io.msgs.v2.entity;

import org.json.JSONObject;

import java.util.Date;

import io.msgs.common.APIUtils;

/**
 * Channel entity.
 */
public class Channel extends AbstractEntity {

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
     * Get the date when channel was created.
     */
    public Date getCreatedAt() {
        return APIUtils.getDate(_data, "createdAt", null);
    }

    /**
     * Set date when channel was created.
     */
    public Channel setCreatedAt(Date createdAt) {
        _putDate("createdAt", createdAt);
        return this;
    }

    /**
     * Get date when channel was updated last time.
     */
    public Date getUpdatedAt() {
        return APIUtils.getDate(_data, "updatedAt", null);
    }

    /**
     * Set date when channel was updated last time.
     *
     * @param updatedAt When the channel was updated the last time.
     */
    public Channel setUpdatedAt(Date updatedAt) {
        _putDate("updatedAt", updatedAt);
        return this;
    }

    /**
     * Get date when last notification was sent on this channel.
     */
    public Date getLastNotificationAt() {
        return APIUtils.getDate(_data, "lastNotificationAt", null);
    }

    /**
     * Set date when last notification was sent on this channel.
     *
     * @param lastNotificationAt When last notification was sent on this channel.
     */
    public Channel setLastNotificationAt(Date lastNotificationAt) {
        _putDate("lastNotificationAt", lastNotificationAt);
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
