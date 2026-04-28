package com.wisecloud.mdm.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int httpStatus;

    public BusinessException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
