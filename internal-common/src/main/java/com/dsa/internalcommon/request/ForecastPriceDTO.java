package com.dsa.internalcommon.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForecastPriceDTO {

    private String depLongitude;
    private String depLatitude;
    private String destLongitude;
    private String destLatitude;


}
