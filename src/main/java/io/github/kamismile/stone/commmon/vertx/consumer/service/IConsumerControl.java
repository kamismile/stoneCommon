package io.github.kamismile.stone.commmon.vertx.consumer.service;

import io.github.kamismile.stone.commmon.base.AppRequest;
import io.github.kamismile.stone.commmon.base.ResultVO;
import io.github.kamismile.stone.commmon.vertx.consumer.VertxAddress;

public interface IConsumerControl {

    @VertxAddress(value = "consumer@version@syn", isPrefix = 0)
    public ResultVO consumerVersionSyn(AppRequest<String> msg);

}
