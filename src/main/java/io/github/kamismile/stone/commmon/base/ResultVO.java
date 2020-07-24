package io.github.kamismile.stone.commmon.base;


import io.github.kamismile.stone.commmon.base.pagination.PaginationInfo;

import java.io.Serializable;

public class ResultVO<T> implements Serializable {
    private String code ;
    private boolean success;
    private String message;
    private T data;
    private PaginationInfo page;

    public ResultVO() {
        super();
    }

    public PaginationInfo getPage() {
        return page;
    }

    public void setPage(PaginationInfo page) {
        this.page = page;
    }


    public ResultVO(String code, String message, T data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
        resetSuccess();
    }

    public ResultVO(String code, T data) {
        super();
        this.code = code;
        this.data = data;
        resetSuccess();
    }

    public ResultVO(String code, String message) {
        super();
        this.code = code;
        this.message = message;
        resetSuccess();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    private boolean resetSuccess() {
        this.success = false;
        if (this.code.equals("0")) {
            this.success = true;
        }
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return this.success;
    }

}
