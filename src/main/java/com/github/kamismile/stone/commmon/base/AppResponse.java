package com.github.kamismile.stone.commmon.base;

public class AppResponse {

    private ResultVO resultVO;

    private boolean success;

    public ResultVO getResultVO() {
        return resultVO;
    }

    public void setResultVO(ResultVO resultVO) {
        this.resultVO = resultVO;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
