package com.wiseasy.openapi.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Base response class for all WiseCloud API responses.
 */
@Getter
@Setter
public class BaseResponse {

    private String code;
    private String msg;

    /**
     * Returns true if the API call was successful (code == "0").
     */
    public boolean isSuccess() {
        return "0".equals(code);
    }
}
