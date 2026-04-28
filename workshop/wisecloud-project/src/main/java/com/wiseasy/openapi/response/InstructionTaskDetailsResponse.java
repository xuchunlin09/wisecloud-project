package com.wiseasy.openapi.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response from instruction/task/details API.
 */
@Getter
@Setter
public class InstructionTaskDetailsResponse extends BaseResponse {

    private List<DeviceExecutionRecord> list;

    @Getter
    @Setter
    public static class DeviceExecutionRecord {
        private String sn;
        private Integer instructionStatus;  // 1=preparing, 2=executing, 3=successful, 4=failed
        private String executeCode;
        private String executeMessage;
    }
}
