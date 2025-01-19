package com.dsa.servicepassengeruser.dao;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
@Data
public class PassengerUser {

    private Long id;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private String passengerPhone;
    private String passengerName;
    private Integer passengerGender;
    private Integer state;

}
