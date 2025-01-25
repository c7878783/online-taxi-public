package com.dsa.serviceprice.mapper;

import com.dsa.internalcommon.pojo.PassengerUser;
import com.dsa.internalcommon.pojo.PriceRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PriceRuleMapper {


    List<PriceRule> selectByMap(Map<String, String> map);


}
