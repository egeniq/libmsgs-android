package io.msgs.common;

/**
 * Exception thrown when there was an error executing the API call.
 * Created by Daniel Zolnai on 2017-10-17.
 */
public class APIException extends Exception {

    public APIException(Exception ex) {
        super(ex);
    }

    public APIException(String message) {
        super(message);
    }
}
