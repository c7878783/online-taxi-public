package com.dsa.internalcommon.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequest {
    @TableId(type = IdType.AUTO)//这个注解确保主键id在mapper调用insert方法后可以被回填
    private Long orderId;
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
    private String fareType;//设定为城市address、车型vehicleType、二合一相加字符串

    private Integer fareVersion;
    //请求设备唯一码
    private String deviceCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime toPickUpPassengerTime; // 司机去接乘客出发时间
    private String toPickUpPassengerLongitude; // 去接乘客时，司机的经度
    private String toPickUpPassengerLatitude; // 去接乘客时，司机的纬度
    private String toPickUpPassengerAddress; // 去接乘客时，司机的地点
}
