package com.wisecloud.mdm.dto.request;

import java.util.List;

public record InstallTaskRequest(
    String taskName,
    String versionMD5,
    String versionNumber,
    String versionName,
    String appName,
    List<String> snList
) {}
