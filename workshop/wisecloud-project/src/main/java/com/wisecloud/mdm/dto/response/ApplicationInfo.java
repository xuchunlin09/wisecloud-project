package com.wisecloud.mdm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO representing an uploaded application in the application list.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationInfo {

    private Long id;
    private String appName;
    private String appPackage;
    private String versionNumber;
    private String versionName;
    private String versionMd5;
    private String appAlias;
    private String appDescription;
    private LocalDateTime uploadedAt;
}
