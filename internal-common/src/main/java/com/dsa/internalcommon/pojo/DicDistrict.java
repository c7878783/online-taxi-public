package com.dsa.internalcommon.pojo;

import lombok.Data;

@Data
public class DicDistrict {

    public String addressCode;
    public String addressName;
    public String parentAddressCode;
    public Integer level;
}
