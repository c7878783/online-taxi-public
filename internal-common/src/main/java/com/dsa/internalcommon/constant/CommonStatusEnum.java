package com.dsa.internalcommon.constant;

import lombok.Getter;
import lombok.Setter;

public enum CommonStatusEnum {
    /**
     * 验证码错误提示：1000-1099
     */
    VERIFY_CODE_ERROR(1099, "验证码错误"),
    /**
     * 成功
     */
    SUCCESS(1, "成功"),
    /**
     * 失败
     */
    FAIL(0, "失败"),
    /**
     * Token类提示：1100-1199
     */
    TOKEN_ERROR(1199,"token错误"),
    /**
     * 用户提示：1200-1299
     */
    USER_NOT_EXISTS(1299, "当前用户不存在"),
    /**
     * 计价规则类提示：1300-1399
     */
    PRICE_RULE_EMPTY(1399, "计价规则不存在")
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
