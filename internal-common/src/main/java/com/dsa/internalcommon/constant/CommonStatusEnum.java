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
    PRICE_RULE_EMPTY(1399, "计价规则不存在"),
    /**
     * 地图信息：1400-1499
     */
    MAP_DISTRICT_ERROR(1400, "请求地图错误"),
    /**
     * 司机和车辆关系
     */
    DRIVER_CAR_BIND_NOT_EXISTS(1500, "司机和车辆绑定关系不存在"),
    DRIVER_CAR_BIND_EXISTS(1501, "司机和车辆绑定关系已存在，请勿重复绑定"),
    DRIVER_NOT_EXISTS(1502, "司机不存在"),
    DRIVER_BIND_EXISTS(1503, "司机已绑定车辆，请先解绑"),
    CAR_BIND_EXISTS(1504, "车辆已被其他司机绑定，请先解绑"),
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
