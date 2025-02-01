package com.dsa.serviceorder.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.PriceRule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-price")
public interface ServiceClient {

    @GetMapping("/price-rule/get-newest-version")
    public ResponseResult<PriceRule> getNewestVersion(@RequestParam String fareType);

    @GetMapping("/price-rule/is-new")
    public ResponseResult<Boolean> isNew(@RequestParam String fareType, @RequestParam Integer fareVersion);

}
