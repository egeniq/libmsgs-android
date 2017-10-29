package io.msgs.v2.entity;

import org.json.JSONObject;

/**
 * Subscription entity.
 */
public class Subscription extends AbstractEntity {
    /**
     * Constructor.
     */
    public Subscription() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param data
     */
    public Subscription(JSONObject data) {
        super(data);
    }

    /**
     * Get channel.
     */
    public Channel getChannel() {
        if (_getObject("channel") != null) {
            return new Channel(_getObject("channel"));
        } else {
            return null;
        }
    }

    /**
     * Set channel.
     */
    public Subscription setChannel(Channel channel) {
        _putObject("channel", channel != null ? channel._data : null);
        return this;
    }
}
