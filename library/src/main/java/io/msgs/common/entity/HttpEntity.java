package io.msgs.common.entity;

import java.io.OutputStream;

/**
 * Entity which is used as input for the API client.
 * Created by Daniel Zolnai on 2017-10-17.
 */
public interface HttpEntity {
    String getBody() throws Exception;
}
