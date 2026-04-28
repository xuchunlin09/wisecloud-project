package com.wiseasy.openapi.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Request for instruction/task/push API — push instruction tasks to devices.
 */
@Getter
@Setter
public class InstructionTaskPushRequest {

    private String version = "v1.0";
    private String instructionKey;
    private Object type;        // InstructionTypeEnum or int
    private Object target;      // List<String> for batch push, Map for single/tag push
    private Map<String, Object> param;
}
