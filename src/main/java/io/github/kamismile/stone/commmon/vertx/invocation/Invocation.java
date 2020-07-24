package io.github.kamismile.stone.commmon.vertx.invocation;

import io.github.kamismile.stone.commmon.vertx.handler.IHandler;
import io.github.kamismile.stone.commmon.vertx.starter.AsyncResponse;

import java.util.List;

public class Invocation  extends  InvocationContext{
    private boolean sync = true;
    // handler链，是arrayList，可以高效地通过index访问
    private List<IHandler> IHandlerList;

    private int handlerIndex=0;

    private int timeout;

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public void next(AsyncResponse asyncResp) throws Exception {
        if(handlerIndex==IHandlerList.size()) {
            return;
        }

        // 不必判断有效性，因为整个流程都是内部控制的
        int runIndex = handlerIndex;
        handlerIndex++;
        IHandlerList.get(runIndex).handle(this, asyncResp);
    }

    public List<IHandler> getIHandlerList() {
        return IHandlerList;
    }

    public void setIHandlerList(List<IHandler> IHandlerList) {
        this.IHandlerList = IHandlerList;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
