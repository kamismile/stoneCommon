package com.github.kamismile.stone.commmon.exception;

/**
 * Created by lidong on 2017/2/21.
 */
public class BusinessException extends RuntimeException {
    private int code = -1;
    private Object[] args;
    private Object data;

    public int getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }

    public BusinessException(int code) {
        super(String.valueOf(code));
        this.code = code;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, Object[] args) {
        super(String.valueOf(code));
        this.code = code;
        this.args = args;
    }

    public BusinessException(int code, Object[] args, Object data) {
        super(String.valueOf(code));
        this.code = code;
        this.args = args;
        this.data = data;
    }

    public BusinessException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public BusinessException(String message) {
        super(message);
    }

    public Object getData() {
        return data;
    }




}
