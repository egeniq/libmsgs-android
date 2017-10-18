package io.msgs.common.entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.msgs.common.APIUtils;

/**
 * Entity which is created with form encoding.
 * Created by Daniel Zolnai on 2017-10-17.
 */
public class UrlEncodedFormEntity implements HttpEntity {
    private Map<String, String> _body;

    public UrlEncodedFormEntity(Map<String, String> body) {
        _body = new HashMap<>(body);
    }

    @Override
    public String getBody() throws IOException {
        if (_body == null) {
            return null;
        }
        return APIUtils.createQueryString(_body);
    }
}
