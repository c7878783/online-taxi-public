package com.dsa.serviceorder.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.TerminalResponse;
import com.dsa.internalcommon.responese.TrsearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("service-map")
public interface ServiceMapClient {

    @PostMapping("/terminal/aroundsearch")
    public ResponseResult<List<TerminalResponse>> aroundSearch(@RequestParam String center, @RequestParam Integer radius);

    @PostMapping("/terminal/trsearch")//POST 请求的 表单参数，Spring 会自动解析，所以可以省略 @RequestParam
    public ResponseResult<TrsearchResponse> trsearch(@RequestParam String tid, @RequestParam Long starttime, @RequestParam Long endtime);

}
