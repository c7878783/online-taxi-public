package com.dsa.ssedriverclientweb.controller;

import com.dsa.internalcommon.util.SsePrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class SseController {

    public static Map<String, SseEmitter> sseEmitterMap = new HashMap();

    /**
     * 建立连接
     * @param userId 用户id
     * @param identity 用户身份
     * @return
     */
    @GetMapping("/connect")
    public SseEmitter connect(@RequestParam Long userId, @RequestParam String identity){

//        long longUserId = Long.parseLong(userId);

        log.info("用户Id:"+userId+"|"+"用户身份："+identity);

        SseEmitter sseEmitter = new SseEmitter(0L);
        String sseMapKey = SsePrefixUtils.generatorSseKey(userId, identity);

        sseEmitterMap.put(sseMapKey, sseEmitter);

        return sseEmitter;
    }

    /**
     * 发送消息
     * @param userId
     * @param identity
     * @param content 消息内容
     * @return
     */
    @GetMapping("/push")
    public String push(@RequestParam Long userId, @RequestParam String identity,@RequestParam String content){
        String sseMapKey = SsePrefixUtils.generatorSseKey(userId, identity);
        try {
            if (sseEmitterMap.containsKey(sseMapKey)) {
                sseEmitterMap.get(sseMapKey).send(content);
            }else {
                return "推送失败";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "给用户："+sseMapKey+"发送了消息";
    }

    /**
     * 关闭连接
     * @param userId
     * @param identity
     * @return
     */
    @GetMapping("/close")
    public String close(@RequestParam String userId, @RequestParam String identity){

        long longUserId = Long.parseLong(userId);

        String sseMapKey = SsePrefixUtils.generatorSseKey(longUserId, identity);
        System.out.println(sseMapKey + "关闭连接");
        if (sseEmitterMap.containsKey(sseMapKey)) {
            sseEmitterMap.remove(sseMapKey);
        }

        return "close成功";
    }

}
