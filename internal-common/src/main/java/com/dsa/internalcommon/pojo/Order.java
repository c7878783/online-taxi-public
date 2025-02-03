package com.dsa.internalcommon.pojo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data// 请替换为你的实际表名
@TableName("order_info")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id; // 订单ID

    private Long passengerId; // 乘客ID
    private String passengerPhone; // 乘客手机号

    private Long driverId; // 司机ID
    private String driverPhone; // 司机手机号

    private Long carId; // 车辆ID
    private String vehicleType; // 车辆类型

    private String address; // 发起地行政区划代码

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime; // 订单发起时间

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departTime; // 预计用车时间

    private String departure; // 预计出发地点详细地址
    private String depLongitude; // 预计出发地点经度
    private String depLatitude; // 预计出发地点纬度

    private String destination; // 预计目的地
    private String destLongitude; // 预计目的地经度
    private String destLatitude; // 预计目的地纬度

    private Integer encrypt; // 坐标加密标识（1:GCJ-02, 2:WGS84, 3:BD-09, 4:CGCS2000, 0:其他）

    private String fareType; // 运价类型编码
    private Integer fareVersion;

    private String receiveOrderCarLongitude; // 接单时车辆经度
    private String receiveOrderCarLatitude; // 接单时车辆纬度

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveOrderTime; // 接单时间，派单成功时间

    private String licenseId; // 机动车驾驶证号
    private String vehicleNo; // 车辆号牌

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime toPickUpPassengerTime; // 司机去接乘客出发时间
    private String toPickUpPassengerLongitude; // 去接乘客时，司机的经度
    private String toPickUpPassengerLatitude; // 去接乘客时，司机的纬度
    private String toPickUpPassengerAddress; // 去接乘客时，司机的地点

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime driverArrivedDepartureTime; // 司机到达上车点时间

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pickUpPassengerTime; // 接到乘客，乘客上车时间

    private String pickUpPassengerLongitude; // 接到乘客，乘客上车经度
    private String pickUpPassengerLatitude; // 接到乘客，乘客上车纬度

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime passengerGetoffTime; // 乘客下车时间

    private String passengerGetoffLongitude; // 乘客下车经度
    private String passengerGetoffLatitude; // 乘客下车纬度

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime; // 订单撤销时间

    private Integer cancelOperator; // 撤销发起者（1:乘客, 2:驾驶员, 3:平台公司）
    private Integer cancelTypeCode; // 撤销类型代码（1:乘客提前撤销, 2:驾驶员提前撤销, 3:平台公司撤销, 4:乘客违约撤销, 5:驾驶员违约撤销）

    private Long driveMile; // 载客里程（米）
    private Long driveTime; // 载客时间（分）

    private Integer orderStatus; // 订单状态（1:订单开始, 2:司机接单, 3:去接乘客, 4:司机到达乘客起点, 5:乘客上车, 6:到达目的地, 7:发起收款, 8:支付完成, 9:订单取消）

    private Double price; // 订单价格

    private LocalDateTime gmtCreate; // 创建时间
    private LocalDateTime gmtModified; // 修改时间
}

