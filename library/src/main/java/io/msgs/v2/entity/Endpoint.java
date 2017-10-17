package io.msgs.v2.entity;

import org.json.JSONObject;

/**
 * Endpoint entity.
 */
public class Endpoint extends AbstractEntity {
    /**
     * Constructor.
     */
    public Endpoint() {
        super();
    }

    /**
     * Constructor.
     *
     * @param data
     */
    public Endpoint(JSONObject data) {
        super(data);
    }

    /**
     * Get Token.
     */
    public String getToken() {
        return _getString("token");
    }

    /**
     * Set Token.
     */
    public Endpoint setToken(String token) {
        _putString("token", token);
        return this;
    }

    /**
     * Get Type
     */
    public String getType() {
        return _getString("type");
    }

    /**
     * Set Type.
     */
    public Endpoint setType(String type) {
        _putString("type", type);
        return this;
    }

    /**
     * Get Adddress.
     */
    public String getAddress() {
        return _getString("address");
    }

    /**
     * Set Address.
     */
    public Endpoint setAddress(String address) {
        _putString("address", address);
        return this;
    }

    /**
     * Get Name.
     */
    public String getName() {
        return _getString("name");
    }

    /**
     * Set Name.
     */
    public Endpoint setName(String name) {
        _putString("name", name);
        return this;
    }

    /**
     * Set EndpointSubscriptionsActive.
     */
    public Boolean getEndpointSubscriptionsActive() {
        return _getBoolean("endpointSubscriptionsActive");
    }

    /**
     * Get EndpointSubscriptionsActive.
     */
    public Endpoint setEndpointSubscriptionsActive(Boolean endpointSubscriptionsActive) {
        _putBoolean("endpointSubscriptionsActive", endpointSubscriptionsActive);
        return this;
    }

    /**
     * Get UserSubscriptionsActive.
     */
    public Boolean getUserSubscriptionsActive() {
        return _getBoolean("userSubscriptionsActive");
    }

    /**
     * Set UserSubscriptionsActive.
     */
    public Endpoint setUserSubscriptionsActive(Boolean userSubscriptionsActive) {
        _putBoolean("userSubscriptionsActive", userSubscriptionsActive);
        return this;
    }

    /**
     * Get delivery frequency
     *
     * @return The frequency value stored for this endpoint
     */
    public String getDeliveryFrequency() {
        return _getString("deliveryFrequency");
    }

    /**
     * Set delivery frequency
     * Valid values: 'direct', 'daily', 'weekly', 'monday', tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'
     *
     * @param frequency The frequency value to store for this endpoint
     */
    public Endpoint setDeliveryFrequency(String frequency) {
        _putString("deliveryFrequency", frequency);
        return this;
    }

    /**
     * Get Data.
     */
    public JSONObject getData() {
        return _getObject("data");
    }

    /**
     * Set Data.
     */
    public Endpoint setData(JSONObject data) {
        _putObject("data", data);
        return this;
    }
}
