package com.egeniq.follow_lib_android.data.entity;

import io.msgs.v2.entity.Subscription;

/**
 * Small wrapper class to make the RTL specific properties easy accessible.
 */
public class FollowSubscription {
    private final Subscription _subscription;

    /**
     * Constructor.
     *
     * @param subscription
     */
    public FollowSubscription(Subscription subscription) {
        _subscription = subscription;
    }

    /**
     * Get channel code.
     */
    public String getCode() {
        return _subscription.getChannel().getCode();
    }

    /**
     * Get name.
     */
    public String getName() {
        return _subscription.getChannel().getName();
    }

    /**
     * Get contentitem guid.
     */
    public int getGuid() {
        return _subscription.getChannel().getData().optInt("id", -1);
    }

    /**
     * Get contentitem type.
     */
    public String getType() {
        return _subscription.getChannel().getData().optString("type", null);
    }
}
