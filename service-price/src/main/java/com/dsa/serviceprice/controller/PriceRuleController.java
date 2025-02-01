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

    /**
     * 添加计价规则
     * @param priceRule
     * @return
     */
    @PostMapping("/add")
    public ResponseResult add(@RequestBody PriceRule priceRule){

        return priceRuleService.add(priceRule);
    }

    /**
     * 更新现有计价规则
     * @param priceRule
     * @return
     */
    @PostMapping("/update")
    public ResponseResult update(@RequestBody PriceRule priceRule){

        return priceRuleService.update(priceRule);
    }
    @GetMapping("/get-newest-version")
    public ResponseResult<PriceRule> getNewestVersion(@RequestParam String fareType){

        return priceRuleService.getNewestVersion(fareType);
    }

    /**
     * 判断当前fareType下的计价规则是否是罪行
     * @param fareType
     * @param fareVersion
     * @return
     */
    @GetMapping("/is-new")
    public ResponseResult<Boolean> isNew(@RequestParam String fareType, @RequestParam Integer fareVersion){

        return priceRuleService.isNew(fareType, fareVersion);
    }

    /**
     * 判断对应城市编码和车型的计价规则是否存在
     * @param cityCode
     * @param vehicleType
     * @return
     */
    @GetMapping("/if-exists")
    public ResponseResult<Boolean> isExists(@RequestParam String cityCode, @RequestParam String vehicleType){

        return priceRuleService.isExists(cityCode, vehicleType);
    }

}
