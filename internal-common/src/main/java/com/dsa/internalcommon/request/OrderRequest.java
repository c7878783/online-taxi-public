package com.dsa.internalcommon.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequest {

    //乘客Id
    private Long passengerId;
    //乘客手机号
    private String passengerPhone;
    //订单发起地区域代码
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    private String departure;
    private String depLongitude;
    private String depLatitude;
    private String destination;
    private String destLongitude;
    private String destLatitude;
    //坐标加密表示:1:gcj-02,2:wgs84,3:bd-09,4:cgcs2000北斗,0:其他
    private Integer encrypt;
    private String fareType;//设定为城市address、车型vehicleType、版本号fareVersion三合一相加字符串

    private Integer fareVersion;

}
