/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.github.kamismile.stone.commmon.vertx.engine;

/**
 * @author dong.li
 * @version $Id: VertxAppEnum, v 0.1 2020/3/25 16:55 dong.li Exp $
 */
public enum VertxAppEnum {
    //Chassis is Down
    DOWN,
    //Chassis is Starting (progressing)
    STARTING,
    //Chassis is Running
    UP,
    //Chassis is Stopping (progressing)
    STOPPING,
    //Chassis Init Failed
    FAILED
}
