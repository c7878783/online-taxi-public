package com.dsa.servicepassengeruser.mapper;
import java.util.Map;

//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsa.internalcommon.pojo.PassengerUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PassengerUserMapper {
    List<PassengerUser> selectByMap(Map<String, Object> map);

    int insert(PassengerUser passengerUser);
}

