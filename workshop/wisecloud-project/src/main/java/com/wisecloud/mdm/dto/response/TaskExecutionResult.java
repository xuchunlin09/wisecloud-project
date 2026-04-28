package com.wisecloud.mdm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Result of querying task execution details via WiseCloud instruction/task/details API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutionResult {

    private List<DeviceExecutionRecord> records;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceExecutionRecord {
        private String sn;
        private Integer instructionStatus;  // 1=preparing, 2=executing, 3=successful, 4=failed
        private String executeCode;
        private String executeMessage;
    }
}
