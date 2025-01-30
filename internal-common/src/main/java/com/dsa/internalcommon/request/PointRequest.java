package com.dsa.internalcommon.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PointRequest {

    private String tid;

    private String trid;

    private PointDTO[] points;

}
