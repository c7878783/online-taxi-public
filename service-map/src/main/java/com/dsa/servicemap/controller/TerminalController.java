package com.dsa.servicemap.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.TerminalResponse;
import com.dsa.internalcommon.responese.TrsearchResponse;
import com.dsa.servicemap.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/terminal")
public class TerminalController {

    @Autowired
    TerminalService terminalService;
    @PostMapping("/add")
    public ResponseResult<TerminalResponse> add(@RequestParam String name,@RequestParam String desc){

        return terminalService.add(name,desc);
    }

    @PostMapping("/aroundsearch")//POST 请求的 表单参数，Spring 会自动解析，所以可以省略 @RequestParam
    public ResponseResult<List<TerminalResponse>> aroundSearch(@RequestParam String center,@RequestParam Integer radius){

        return terminalService.aroundSearch(center, radius);
    }

    @PostMapping("/trsearch")//POST 请求的 表单参数，Spring 会自动解析，所以可以省略 @RequestParam
    public ResponseResult<TrsearchResponse> trsearch(@RequestParam String tid, @RequestParam Long starttime, @RequestParam Long endtime){

        return terminalService.trsearch(tid, starttime, endtime);
    }

}
