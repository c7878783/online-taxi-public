package com.dsa.internalcommon.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PassengerUser {

    private Long id;//主键
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private String passengerPhone;
    private String passengerName;
    private byte passengerGender;
    private byte state;//帐号状态0注销，1有效
    private String profilePhoto;
}
