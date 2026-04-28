package com.wisecloud.mdm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDetailResponse {
    private String sn;
    private String deviceName;
    private String deviceType;
    private Integer status;
    private Integer onlineStatus;
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
    private List<InstalledAppInfo> installApps;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstalledAppInfo {
        private String appName;
        private String appPackage;
        private String versionName;
    }
}
