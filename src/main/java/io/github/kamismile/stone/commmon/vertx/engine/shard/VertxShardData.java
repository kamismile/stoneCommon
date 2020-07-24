/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.github.kamismile.stone.commmon.vertx.engine.shard;

import io.github.kamismile.stone.commmon.vertx.ServiceVertxConfigProperties;
import io.github.kamismile.stone.commmon.vertx.engine.VertxAppEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dong.li
 * @version $Id: VertxShardData, v 0.1 2020/4/3 16:11 dong.li Exp $
 */


public abstract class VertxShardData {

    protected final Logger logger = LogManager.getLogger(VertxShardData.class);

    private VertxAppEngine vertxAppEngine;

    @Autowired
    private ServiceVertxConfigProperties serviceVertxConfigProperties;

    public VertxShardData(VertxAppEngine vertxAppEngine) {
        this.vertxAppEngine = vertxAppEngine;
    }


    public abstract void registerVertxShardData();


    public VertxAppEngine getVertxAppEngine() {
        return vertxAppEngine;
    }

    public ServiceVertxConfigProperties getServiceVertxConfigProperties() {
        return serviceVertxConfigProperties;
    }
}
