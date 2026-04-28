package com.wiseasy.openapi.client;

import lombok.Getter;

/**
 * Exception thrown by the WiseCloud SDK when an API call fails at the transport/SDK level.
 * Error codes range from -1001 to -1006.
 */
@Getter
public class OpenApiClientException extends Exception {

    private final int code;
    private final String msg;

    public OpenApiClientException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public OpenApiClientException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }
}
