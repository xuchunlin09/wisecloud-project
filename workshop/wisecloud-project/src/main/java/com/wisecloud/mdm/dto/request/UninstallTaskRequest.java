package com.wisecloud.mdm.dto.request;

import java.util.List;

public record UninstallTaskRequest(
    String pkgName,
    List<String> snList
) {}
