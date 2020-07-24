package com.github.kamismile.stone.commmon.vertx.handler;

import com.github.kamismile.stone.commmon.vertx.invocation.Invocation;
import com.github.kamismile.stone.commmon.vertx.starter.AsyncResponse;
import com.github.kamismile.stone.commmon.vertx.definition.InvocationType;

public interface IHandler {

    InvocationType getInvocationType();

    int order();

    void handle(Invocation invocation, AsyncResponse asyncResp) throws Exception;
}
