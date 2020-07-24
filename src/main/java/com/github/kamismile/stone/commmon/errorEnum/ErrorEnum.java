/**
 * LY.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.github.kamismile.stone.commmon.errorEnum;

/**
 * @author dong.li
 * @version $Id: ErrorEnum, v 0.1 2018/11/29 14:17 dong.li Exp $
 */
public enum ErrorEnum {
    //0 正常 非0 异常 -1 ~ 3999 为保留
    //1000 系统
    //2000  系统
    NOSERVICE(3000, "根据配置未找到可调用的服务"),//3000  组件
    NOSERVICEORTIMEOUT(3001, "根据配置未找到可调用的服务或服务调用超时");//3000  组件
    private int code;
    private String message;


    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
