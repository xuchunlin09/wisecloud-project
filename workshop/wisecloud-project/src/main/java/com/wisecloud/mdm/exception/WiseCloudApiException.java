package com.wisecloud.mdm.exception;

import lombok.Getter;

@Getter
public class WiseCloudApiException extends RuntimeException {

    private final int code;

    public WiseCloudApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public WiseCloudApiException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
