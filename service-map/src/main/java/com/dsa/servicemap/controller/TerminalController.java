package com.dsa.servicemap.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.TerminalResponse;
import com.dsa.servicemap.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/terminal")
public class TerminalController {

    @Autowired
    TerminalService terminalService;
    @PostMapping("/add")
    public ResponseResult<TerminalResponse> add(String name, String desc){

        return terminalService.add(name,desc);
    }

    @PostMapping("/aroundsearch")
    public ResponseResult<TerminalResponse> aroundSearch(String center, Integer radius){

        return terminalService.aroundSearch(center, radius);
    }


}
