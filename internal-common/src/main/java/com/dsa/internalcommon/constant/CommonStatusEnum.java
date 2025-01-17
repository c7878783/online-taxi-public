package com.dsa.internalcommon.constant;

import lombok.Getter;
import lombok.Setter;

public enum CommonStatusEnum {

    SUCCESS(1, "成功"),
    FAIL(0, "失败"),

    ;
    @Getter
    private int code;
    @Getter
    private String value;

    CommonStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
