package com.wisecloud.mdm.dto.response;

public record TaskDeviceStatus(
    String sn,
    int instructionStatus,
    String executeCode,
    String executeMessage
) {}
