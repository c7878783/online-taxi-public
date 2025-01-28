package com.dsa.servicedriveruser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsa.internalcommon.pojo.Car;
import org.apache.ibatis.annotations.Mapper;

/**
 * BaseMapper<E>泛型一定要写
 */
@Mapper
public interface CarMapper extends BaseMapper<Car> {



}
