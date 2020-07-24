package io.github.kamismile.stone.commmon.vertx.starter;

import io.github.kamismile.stone.commmon.base.AppResponse;

public interface AsyncResponse {


    void handle(AppResponse response);
}
