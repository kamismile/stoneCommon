package io.github.kamismile.stone.commmon.base;

public class AppRequest<T> {

    private String traceId;

    private String pTraceId;

    private T data;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getpTraceId() {
        return pTraceId;
    }

    public void setpTraceId(String pTraceId) {
        this.pTraceId = pTraceId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
