package com.wiseasy.openapi.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response from application/upload/add API.
 */
@Getter
@Setter
public class ApplicationUploadAddResponse extends BaseResponse {

    private String versionMD5;
    private String versionNumber;
    private String appName;
    private String appPackage;
}
