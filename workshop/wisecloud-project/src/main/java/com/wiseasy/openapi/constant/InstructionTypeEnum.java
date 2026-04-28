package com.wiseasy.openapi.constant;

import lombok.Getter;

/**
 * Instruction push type enum.
 */
@Getter
public enum InstructionTypeEnum {

    SINGLE_PUSH(1, "Single push"),
    BATCH_PUSH(2, "Batch push"),
    TAG_PUSH(3, "Tag push");

    private final int code;
    private final String description;

    InstructionTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static InstructionTypeEnum getByCode(int code) {
        for (InstructionTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
