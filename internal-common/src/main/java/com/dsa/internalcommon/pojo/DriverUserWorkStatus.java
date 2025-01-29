package com.dsa.internalcommon.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DriverUserWorkStatus {
    private Long Id;
    private Long driverId;
    private Integer workStatus;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

}
