package io.github.kamismile.stone.commmon.vertx.handler;

import io.github.kamismile.stone.commmon.vertx.invocation.Invocation;
import io.github.kamismile.stone.commmon.vertx.starter.AsyncResponse;
import io.github.kamismile.stone.commmon.vertx.definition.InvocationType;

public interface IHandler {

    InvocationType getInvocationType();

    int order();

    void handle(Invocation invocation, AsyncResponse asyncResp) throws Exception;
}
