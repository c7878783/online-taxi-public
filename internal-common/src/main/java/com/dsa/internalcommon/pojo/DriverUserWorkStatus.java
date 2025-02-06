package com.dsa.internalcommon.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DriverUserWorkStatus {
    private Long Id;
    private Long driverId;
    private Integer workStatus;//0收车 1出车 2暂停接单
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

}
