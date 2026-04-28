package com.wiseasy.openapi.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Request for verify/sn API — batch check device SN validity.
 */
@Getter
@Setter
public class DeviceVerifySnRequest {

    private String version = "v1.0";
    private Set<String> snList;
}
