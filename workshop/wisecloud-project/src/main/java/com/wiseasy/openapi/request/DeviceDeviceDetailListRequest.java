package com.wiseasy.openapi.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Request for device/detailList API — batch query device details.
 */
@Getter
@Setter
public class DeviceDeviceDetailListRequest {

    private String version = "v1.0";
    private List<String> snList;
}
