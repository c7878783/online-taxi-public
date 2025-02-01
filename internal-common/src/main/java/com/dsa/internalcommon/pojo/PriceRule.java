package com.dsa.internalcommon.pojo;

import lombok.Data;

@Data
public class PriceRule {

    private String cityCode;            // 城市代码
    private String vehicleType;         // 车辆类型
    private Double startFare;           // 起步价
    private Integer startMile;          // 起步里程（公里）
    private Double unitPricePerMile;    // 每公里单价
    private Double unitPricePerMinute;  // 每分钟单价
    private Integer fareVersion;        // 运价版本
    private String fareType;
}
