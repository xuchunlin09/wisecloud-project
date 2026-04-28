package com.wisecloud.mdm.dto.response;

import java.util.List;

public record TaskDetailResponse(
    List<TaskDeviceStatus> completed,
    List<TaskDeviceStatus> failed,
    List<TaskDeviceStatus> executing,
    List<TaskDeviceStatus> preparing
) {
    public boolean isAllTerminal() {
        return executing.isEmpty() && preparing.isEmpty();
    }
}
