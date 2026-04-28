package com.wiseasy.openapi.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response from device/detailList API.
 */
@Getter
@Setter
public class DeviceDeviceDetailListResponse extends BaseResponse {

    private List<DeviceDetail> data;

    @Getter
    @Setter
    public static class DeviceDetail {
        private String sn;
        private String deviceName;
        private String deviceType;
        private Integer status;
        private Integer onlineStatus;       // 1=online, 2=offline
        private String activationTime;
        private String merchantNo;
        private String merchantName;
        private String storeName;
        private String otaVersionName;
        private String electricRate;
        private String wifiStatus;
        private String gprsStatus;
        private String longitude;
        private String latitude;
        private List<InstalledApp> installApps;
    }

    @Getter
    @Setter
    public static class InstalledApp {
        private String appName;
        private String appPackage;
        private String versionName;
    }
}
