package com.dsa.internalcommon.dto;

import com.dsa.internalcommon.constant.CommonStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * DTO（Data Transfer Object） 是一种设计模式，
 * 主要用于在不同的系统层之间传递数据。
 * 它是一个 纯粹的数据容器，通常只包含一些字段（属性）和它们的 getter/setter 方法，而不包含任何业务逻辑。
 * @param <T>
 */
@Data
@Accessors(chain = true)
public class ResponseResult <T>{

    private int code;
    private String message;
    private T data;

    public static <T> ResponseResult success(){
        return new ResponseResult().setCode(CommonStatusEnum.SUCCESS.getCode()).setMessage(CommonStatusEnum.SUCCESS.getValue());
    }

    public static <T> ResponseResult success(T data){
        return new ResponseResult().setCode(CommonStatusEnum.SUCCESS.getCode()).setMessage(CommonStatusEnum.SUCCESS.getValue()).setData(data);
    }

    public static <T> ResponseResult  fail(T data){
        return new ResponseResult().setData(data);
    }

    public static ResponseResult fail(int code, String message){
        return new ResponseResult().setCode(code).setMessage(message);
    }

    public static ResponseResult fail(int code, String message, String data){
        return new ResponseResult().setCode(code).setMessage(message).setData(data);
    }

}
