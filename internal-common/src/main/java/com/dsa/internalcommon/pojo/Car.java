package com.dsa.internalcommon.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Car {

    private long id;//主键
    private String address;
    private String vehicleNo;
    private String plateColor;
    private Integer seats;
    private String brand;
    private String model;
    private String vehicleType;
    private String ownerName;
    private String vehicleColor;
    private String engineId;
    private String vin;
    private LocalDate certifyDateA;
    private String fuelType;
    private String engineDisplace;
    private String transAgency;
    private String transArea;
    private LocalDate transDateStart;
    private LocalDate transDateStop;
    private LocalDate certifyDateB;
    private String fixState;
    private String checkState;
    private String feePrintId;
    private String gpsBrand;
    private String gpsModel;
    private LocalDate nextFixDate;
    private LocalDate gpsInstallDate;
    private LocalDate registerDate;
    private Integer commercialType;
    private String fareType;
    private Integer state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    private String tid;

}
