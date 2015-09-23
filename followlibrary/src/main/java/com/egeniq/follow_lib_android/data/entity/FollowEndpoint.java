package com.egeniq.follow_lib_android.data.entity;

import io.msgs.v2.entity.Endpoint;

/**
 * Small wrapper class to make the RTL specific properties easy accessible.
 */
public class FollowEndpoint {
    private final Endpoint _endpoint;

    /**
     * Cosntructor.
     *
     * @param endpoint
     */
    public FollowEndpoint(Endpoint endpoint) {
        _endpoint = endpoint;
    }

    /**
     * Endpoint token.
     *
     * @return Token.
     */
    public String getToken() {
        return _endpoint.getToken();
    }

    /**
     * Endpoint name.
     *
     * @return Name.
     */
    public String getName() {
        return _endpoint.getName();
    }

    /**
     * Receives breaking news?
     *
     * @return Receive breaking news?
     */
    public boolean getReceivesBreakingNews() {
        return _endpoint.getEndpointSubscriptionsActive();
    }

    /**
     * Set wheter the endpoint receives breaking news.
     *
     * @param receive Receive breaking news
     */
    public void setReceivesBreakingNews(boolean receive) {
        _endpoint.setEndpointSubscriptionsActive(receive);
    }

    /**
     * Receives follow updates?
     *
     * @return Receive follow updates?
     */
    public boolean getReceivesUpdates() {
        return _endpoint.getUserSubscriptionsActive();
    }

    /**
     * Set wheter the endpoint receives follow updates.
     *
     * @param receive Receive follow updates
     */
    public void setReceivesUpdates(boolean receive) {
        _endpoint.setUserSubscriptionsActive(receive);
    }
}
