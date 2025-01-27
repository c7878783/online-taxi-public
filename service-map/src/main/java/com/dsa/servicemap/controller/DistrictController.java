package com.dsa.servicemap.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.servicemap.remote.MapDicDistrictClient;
import com.dsa.servicemap.service.DicDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DistrictController {

    @Autowired
    DicDistrictService dicDistrictService;

    @GetMapping("/dic-district")
    public ResponseResult initDistrict(String keywords){


        return dicDistrictService.initDistrict(keywords);
    }
}
