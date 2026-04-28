package com.wiseasy.openapi.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response from instruction/task/push API.
 */
@Getter
@Setter
public class InstructionTaskPushResponse extends BaseResponse {

    private String traceId;
}
