package com.dsa.servicemap.controller;

import com.dsa.internalcommon.pojo.DicDistrict;
import com.dsa.servicemap.mapper.DicDistrictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class TestController {
    @Autowired
    DicDistrictMapper dicDistrictMapper;

    @GetMapping("/test")
    public String testMap(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("address_code", 110000);
        List<DicDistrict> dicDistricts = dicDistrictMapper.selectByMap(map);
        System.out.println(dicDistricts);


        return "testing";

    }

}
