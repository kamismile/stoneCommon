/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.github.kamismile.stone.commmon.vertx.exception;

import io.vertx.core.eventbus.Message;

/**
 * @author dong.li
 * @version $Id: VertxExException, v 0.1 2020/3/26 16:32 dong.li Exp $
 */
public interface VertxExException {

    void handle(String address, Message message, Throwable e);
}
