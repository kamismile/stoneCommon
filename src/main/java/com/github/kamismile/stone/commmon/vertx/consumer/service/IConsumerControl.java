package com.github.kamismile.stone.commmon.vertx.consumer.service;

import com.github.kamismile.stone.commmon.base.AppRequest;
import com.github.kamismile.stone.commmon.base.ResultVO;
import com.github.kamismile.stone.commmon.vertx.consumer.VertxAddress;

public interface IConsumerControl {

    @VertxAddress(value = "consumer@version@syn", isPrefix = 0)
    public ResultVO consumerVersionSyn(AppRequest<String> msg);

}
