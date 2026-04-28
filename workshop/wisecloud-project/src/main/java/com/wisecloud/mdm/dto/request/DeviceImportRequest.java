package com.wisecloud.mdm.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeviceImportRequest(
        @NotEmpty(message = "SN列表不能为空")
        List<String> snList
) {}
