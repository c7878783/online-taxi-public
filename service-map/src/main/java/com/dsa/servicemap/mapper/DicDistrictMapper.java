package com.dsa.servicemap.mapper;

import com.dsa.internalcommon.pojo.DicDistrict;
import com.dsa.internalcommon.pojo.PassengerUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DicDistrictMapper {
    List<DicDistrict> selectByMap(Map<String, Object> map);
}
