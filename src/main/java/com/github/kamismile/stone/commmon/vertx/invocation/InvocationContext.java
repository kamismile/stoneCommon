package com.github.kamismile.stone.commmon.vertx.invocation;

import com.github.kamismile.stone.commmon.vertx.definition.VertxProviderMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InvocationContext {
    protected Map<String, Object> context = new HashMap<>();

    private String serverName;

    private String serverVersion;

    private String serverAddress;

    private VertxProviderMeta vertxProviderMeta;

    public VertxProviderMeta getVertxProviderMeta() {
        if (Objects.isNull(vertxProviderMeta)) {
            vertxProviderMeta = new VertxProviderMeta(this.getServerName(), this.getServerAddress(), this.getServerVersion());
        }
        return vertxProviderMeta;
    }


    public Map<String, Object> getContext() {
        return context;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void addContext(String key, Object value) {
        context.put(key, value);
    }

    public Object getContext(String key) {
        return context.get(key);
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
