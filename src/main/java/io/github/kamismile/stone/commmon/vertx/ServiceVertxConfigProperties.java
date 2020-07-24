package io.github.kamismile.stone.commmon.vertx;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lidong on 2017/2/23.
 */

/**
 * Created by lidong on 2017/2/23.
 */
@ConfigurationProperties(prefix = "spring.ex-vertx")
public class ServiceVertxConfigProperties {

    private String name;
    private String password;
    private int workers;
    private int port;
    private String host;
    private String clusterPublicHost;
    private int devWorkers;
    private static String groupCount;
    private static String coreThreads;
    private static String maxThreads;
    private static String maxIdleInSecond;
    private static String maxQueueSize;
    private final Map<String, String> userAttributes = new HashMap<String, String>();

    private final Map<String, VertxServerProvider> servers = new HashMap<String, VertxServerProvider>();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClusterPublicHost() {
        return clusterPublicHost;
    }

    public void setClusterPublicHost(String clusterPublicHost) {
        this.clusterPublicHost = clusterPublicHost;
    }

    public static String getGroupCount() {
        return groupCount;
    }

    public static String getCoreThreads() {
        return coreThreads;
    }

    public static String getMaxThreads() {
        return maxThreads;
    }

    public static String getMaxIdleInSecond() {
        return maxIdleInSecond;
    }

    public static String getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setGroupCount(String groupCount) {
        ServiceVertxConfigProperties.groupCount = groupCount;
    }

    public void setCoreThreads(String coreThreads) {
        ServiceVertxConfigProperties.coreThreads = coreThreads;
    }

    public void setMaxThreads(String maxThreads) {
        ServiceVertxConfigProperties.maxThreads = maxThreads;
    }

    public void setMaxIdleInSecond(String maxIdleInSecond) {
        ServiceVertxConfigProperties.maxIdleInSecond = maxIdleInSecond;
    }

    public void setMaxQueueSize(String maxQueueSize) {
        ServiceVertxConfigProperties.maxQueueSize = maxQueueSize;
    }

    public Map<String, String> getUserAttributes() {
        return userAttributes;
    }

    public int getDevWorkers() {
        return devWorkers;
    }

    public void setDevWorkers(int devWorkers) {
        this.devWorkers = devWorkers;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getWorkers() {
        return workers;
    }

    public void setWorkers(int workers) {
        this.workers = workers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    private List<String> members;

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getMembers() {
        return members;
    }

    public Map<String, VertxServerProvider> getServers() {
        return servers;
    }

    public static class VertxServerProvider {
        private String serverName;
        private String serverVersion; // XXX.XXX.XXX.XXX  0.0.0.1 格式
        private int timeout;
        private List<String> basePackages;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public List<String> getBasePackages() {
            return basePackages;
        }

        public void setBasePackages(List<String> basePackages) {
            this.basePackages = basePackages;
        }

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getServerVersion() {
            return serverVersion;
        }

        public void setServerVersion(String serverVersion) {
            this.serverVersion = serverVersion;
        }
    }


}
