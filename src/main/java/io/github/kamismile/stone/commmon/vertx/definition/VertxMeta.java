/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.github.kamismile.stone.commmon.vertx.definition;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;

/**
 * @author dong.li
 * @version $Id: VertxMeta, v 0.1 2020/3/26 14:46 dong.li Exp $
 */
public class VertxMeta {

    private String appName;

    private String appAddress;

    private String appVersion;

    private String hostName;

    private String hostAddress;

    private String address = "{0}@{1}@{2}";

    private String exInfo="";

    private String pId;

    private String nodeID;

    public VertxMeta(String appName, String appAddress, String appVersion) throws UnknownHostException {
        this.appName = appName;
        this.appAddress = appAddress;
        this.appVersion = appVersion;
        this.address = MessageFormat.format(this.address, appName, appAddress, appVersion);

        getIp();

        getPID();
    }

    private void getPID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.pId=runtimeMXBean.getName();
    }

    private void getIp() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        this.hostName = address.getHostName();
        this.hostAddress = address.getHostAddress();
    }

    public String getAddress() {
        return address + exInfo;
    }

    public void setExInfo(String exInfo) {
        this.exInfo = exInfo;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppAddress() {
        return appAddress;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getHostName() {
        return hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getExInfo() {
        return exInfo;
    }

    public String getpId() {
        return pId;
    }

    public String getNodeID() {
        return nodeID;
    }
}
