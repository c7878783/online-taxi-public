package com.dsa.internalcommon.responese;

import lombok.Data;

@Data
public class DriverUserExistsResponse {

    private String driverPhone;
    public int ifexists;
}
