package io.msgs.v2.entity;

import org.json.JSONObject;

/**
 * User entity.
 */
public class User extends AbstractEntity {
    /**
     * Constructor.
     */
    public User() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param properties
     */
    public User(JSONObject data) {
        super(data);
    }

    /**
     * Get token.
     */
    public String getToken() {
        return _getString("token");
    }

    /**
     * Sets the token.
     */
    public User setToken(String token) {
        _putString("token", token);
        return this;
    }

    /**
     * Get external user identifier.
     */
    public String getExternalUserId() {
        return _getString("externalUserId");
    }

    /**
     * Sets the external user identifier.
     */
    public User setExternalUserId(String externalUserId) {
        _putString("externalUserId", externalUserId);
        return this;
    }
}
