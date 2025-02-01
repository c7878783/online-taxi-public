package com.dsa.serviceprice.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.PriceRule;
import com.dsa.serviceprice.service.PriceRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/price-rule")
public class PriceRuleController {

    @Autowired
    PriceRuleService priceRuleService;

    @PostMapping("/add")
    public ResponseResult add(@RequestBody PriceRule priceRule){

        return priceRuleService.add(priceRule);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody PriceRule priceRule){

        return priceRuleService.update(priceRule);
    }
    @GetMapping("/get-newest-version")
    public ResponseResult<PriceRule> getNewestVersion(@RequestParam String fareType){

        return priceRuleService.getNewestVersion(fareType);
    }

    @GetMapping("/is-new")
    public ResponseResult<Boolean> isNew(@RequestParam String fareType, @RequestParam Integer fareVersion){

        return priceRuleService.isNew(fareType, fareVersion);
    }

}
