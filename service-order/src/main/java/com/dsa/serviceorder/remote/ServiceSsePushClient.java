package com.dsa.serviceorder.remote;

import com.dsa.internalcommon.util.SsePrefixUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@FeignClient("service-sse-push")
public interface ServiceSsePushClient {

    @GetMapping("/push")
    public String push(@RequestParam Long userId, @RequestParam String identity, @RequestParam String content);

}
