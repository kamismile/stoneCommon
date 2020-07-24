/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.github.kamismile.stone.commmon.vertx.engine;

import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import io.github.kamismile.stone.commmon.vertx.engine.shard.DefaultVertxShardData;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author dong.li
 * @version $Id: VertxMembershipListener, v 0.1 2020/3/27 11:20 dong.li Exp $
 */
public class VertxMembershipListener implements MembershipListener {
    protected final Logger logger = LogManager.getLogger(VertxMembershipListener.class);

    DefaultVertxShardData vertxShardData;
    VertxAppEngine vertxAppEngine;

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public VertxMembershipListener(DefaultVertxShardData vertxShardData, VertxAppEngine vertxAppEngine) {
        this.vertxShardData = vertxShardData;
        this.vertxAppEngine = vertxAppEngine;
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        logger.info("add");
//        synInfo();
    }

    private void synInfo() {
        executorService.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(RandomUtils.nextInt(2,5));
            } catch (InterruptedException e) {

            }
            vertxShardData.getConsumer();
        });
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        logger.info("remove");
        synInfo();
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        logger.info("attribute");
        synInfo();
    }
}
