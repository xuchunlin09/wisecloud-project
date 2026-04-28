package com.wisecloud.mdm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Result of uploading an APK via WiseCloud application/upload/add API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUploadResult {

    private String versionMD5;
    private String versionNumber;
    private String appName;
    private String appPackage;
}
