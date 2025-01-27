package com.dsa.servicedriveruser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsa.internalcommon.pojo.DriverUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DriverUserMapper extends BaseMapper<DriverUser> {


}
