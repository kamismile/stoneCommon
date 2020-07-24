/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.github.kamismile.stone.commmon.vertx.handler.impl.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.kamismile.stone.commmon.util.ValueUtils;
import io.github.kamismile.stone.commmon.vertx.consumer.ConsumerMeta;
import io.github.kamismile.stone.commmon.vertx.definition.InvocationType;
import io.github.kamismile.stone.commmon.vertx.definition.VertxProviderMeta;
import io.github.kamismile.stone.commmon.vertx.handler.IHandler;
import io.github.kamismile.stone.commmon.vertx.invocation.Invocation;
import io.github.kamismile.stone.commmon.vertx.starter.AsyncResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author dong.li
 * @version $Id: ProviderVersionChooseHandleImpl, v 0.1 2020/4/3 10:55 dong.li Exp $
 */
@Component
public class ProviderVersionChooseHandleImpl implements IHandler {

    private Cache<String, Integer> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Override
    public InvocationType getInvocationType() {
        return InvocationType.PRODUCER;
    }

    @Override
    public int order() {
        return 0;
    }


    @Override
    public void handle(Invocation invocation, AsyncResponse asyncResp) throws Exception {
        VertxProviderMeta vertxProviderMeta = invocation.getVertxProviderMeta();

        String version = ConsumerMeta.findVersion(vertxProviderMeta);
        int retry = 0;
        while (StringUtils.isBlank(version) && retry <= 3 && ValueUtils.isNull(cache.getIfPresent(getKey(vertxProviderMeta)))) {
            synchronized (this) {
                version = ConsumerMeta.findVersion(vertxProviderMeta);
                retry++;
                TimeUnit.SECONDS.sleep(1);
            }
        }

        //空 表示没有在缓存中找到
        if (StringUtils.isNotBlank(version)) {
            vertxProviderMeta.setServerVersion(version);
        } else {
            cache.put(getKey(vertxProviderMeta), 0);
        }
        invocation.next(asyncResp);
    }

    private String getKey(VertxProviderMeta vertxProviderMeta) {
        return new StringBuilder(vertxProviderMeta.getServerName())
                .append("~")
                .append(vertxProviderMeta.getServerAddress()).toString();
    }
}
