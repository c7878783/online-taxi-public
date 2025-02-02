package com.dsa.internalcommon.responese;

import lombok.Data;

@Data
public class OrderDriverResponse {

    private Long driverId;

    private String driverPhone;

    private Long carId;

    private String licenseId; // 机动车驾驶证号

    private String vehicleNo; // 车辆号牌

    private String vehicleType;

}
