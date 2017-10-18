package io.msgs.v2.entity;

import org.json.JSONObject;

/**
 * Channel entity.
 * 
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
