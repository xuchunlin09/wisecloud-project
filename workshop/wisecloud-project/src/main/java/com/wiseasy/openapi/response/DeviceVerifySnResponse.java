package com.wiseasy.openapi.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response from verify/sn API.
 */
@Getter
@Setter
public class DeviceVerifySnResponse extends BaseResponse {

    private List<String> sucList;
    private List<SnErrorInfo> errList;

    @Getter
    @Setter
    public static class SnErrorInfo {
        private String sn;
        private String errMsg;
    }
}
