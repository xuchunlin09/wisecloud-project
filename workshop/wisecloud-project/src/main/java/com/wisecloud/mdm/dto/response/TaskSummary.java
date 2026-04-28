package com.wisecloud.mdm.dto.response;

import java.time.LocalDateTime;

public record TaskSummary(
    String traceId,
    String taskName,
    String taskType,
    String targetApp,
    int deviceCount,
    int completedCount,
    int failedCount,
    String progress,
    LocalDateTime createdAt
) {}
