package com.wisecloud.mdm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for APK upload, containing key identifiers returned by WiseCloud.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUploadResponse {

    private String versionMD5;
    private String versionNumber;
    private String appName;
    private String packageName;
}
