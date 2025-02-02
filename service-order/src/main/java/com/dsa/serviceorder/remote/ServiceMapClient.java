package com.dsa.serviceorder.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.TerminalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("service-map")
public interface ServiceMapClient {

    @PostMapping("/terminal/aroundsearch")
    public ResponseResult<List<TerminalResponse>> aroundSearch(@RequestParam String center, @RequestParam Integer radius);

}
