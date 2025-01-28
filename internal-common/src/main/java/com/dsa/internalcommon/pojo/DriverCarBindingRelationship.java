package com.dsa.internalcommon.pojo;

import com.dsa.internalcommon.dto.ResponseResult;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DriverCarBindingRelationship {

    private Long id; // 主键
    private Long driverId; // 司机ID
    private Long carId;
    private Integer bindingState; // 绑定状态
    private LocalDateTime bindingTime; // 绑定时间
    private LocalDateTime unBindingTime; // 解绑时间


}
