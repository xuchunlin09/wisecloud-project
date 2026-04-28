package com.wiseasy.openapi.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Request for instruction/task/details API — query task execution details.
 */
@Getter
@Setter
public class InstructionTaskDetailsRequest {

    private String version = "v1.0";
    private String traceId;
}
