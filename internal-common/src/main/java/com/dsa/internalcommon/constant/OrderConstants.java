package com.dsa.internalcommon.constant;

public class OrderConstants {
    //订单撤销/无效：0
    public static final int ORDER_INVALID = 0;
    //订单开始：1
    public static final int ORDER_START = 1;
    //司机接单：2
    public static final int DRIVER_RECEIVE_ORDER = 2;
    //司机去接乘客：3
    public static final int DRIVER_TO_PICK_UP_PASSENGER = 3;
    //司机到达乘客起点：4
    public static final int DRIVER_ARRIVED_DEPARTURE = 4;
    //司机接到乘客，开始行程：5
    public static final int PICK_UP_PASSENGER = 5;
    //行程结束，未支付：6
    public static final int PASSENGER_GETOFF = 6;
    //发起收款：7
    public static final int TO_START_PAY = 7;
    //支付完成：8
    public static final int SUCCESS_PAY = 8;
    //订单取消：9
    public static final int ORDER_CANCEL = 9;

    //订单撤销类型
    public static final int CANCEL_PASSENGER_BEFORE = 1;
    public static final int CANCEL_DRIVER_BEFORE = 2;
    public static final int CANCEL_PLATFORM_BEFORE = 3;
    public static final int CANCEL_PASSENGER_ILLEGAL = 4;
    public static final int CANCEL_DRIVER_ILLEGAL = 5;

}
