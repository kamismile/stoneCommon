/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.github.kamismile.stone.commmon.vertx.definition;

import java.text.MessageFormat;

/**
 * @author dong.li
 * @version $Id: VertxMeta, v 0.1 2020/3/26 14:46 dong.li Exp $
 */
public class VertxProviderMeta {

    private String serverName;

    private String serverAddress;

    private String serverVersion;

    private String address = "{0}@{1}@{2}";

    public VertxProviderMeta(String serverName, String serverAddress, String serverVersion) {
        this.serverName = serverName;
        this.serverAddress = serverAddress;
        this.serverVersion = serverVersion;
    }


    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getAddress() {
        return MessageFormat.format(this.address, serverName, serverAddress, serverVersion);
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
