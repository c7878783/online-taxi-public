package com.dsa.internalcommon.constant;

public class DriverCarConstants {

    public final static int DRIVER_CAR_BIND = 1;
    public final static int DRIVER_CAR_UNBIND = 2;
    /**
     * 死机状态：1有效
     */
    public final static int DRIVER_STATE_VALID = 1;
    /**
     * 死机状态：0无效
     */
    public final static int DRIVER_STATE_INVALID = 0;
    /**
     * 司机状态：存在是1
     */
    public static int DRIVER_EXISTS = 1;
    /**
     * 司机状态：不存在是
     */
    public static int DRIVER_NOT_EXISTS = 0;
    /**
     * 司机工作状态，收车0，出车1，暂停1
     */
    public static int DRIVER_WORK_STATUS_STOP = 0;
    public static int DRIVER_WORK_STATUS_START = 1;
    public static int DRIVER_WORK_STATUS_SUSPEND = 2;
}
