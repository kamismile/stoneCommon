package com.github.kamismile.stone.commmon.vertx.consumer.service.impl;

import com.github.kamismile.stone.commmon.base.AppRequest;
import com.github.kamismile.stone.commmon.base.ResultVO;
import com.github.kamismile.stone.commmon.vertx.consumer.service.IConsumerControl;
import com.github.kamismile.stone.commmon.vertx.engine.shard.DefaultVertxShardData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.kamismile.stone.commmon.vertx.consumer.BaseControl;

@Component
public class ConsumerControlImp extends BaseControl implements IConsumerControl {


    @Autowired
    private DefaultVertxShardData vertxShardData;

    public ResultVO consumerVersionSyn(AppRequest<String> msg) {
        logger.info("consumer syn");
        vertxShardData.getConsumer();
        return successfulResultVO("");
    }
}
