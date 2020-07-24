package io.github.kamismile.stone.commmon.vertx.provider;

import io.github.kamismile.stone.commmon.vertx.invocation.Invoker;
import io.github.kamismile.stone.commmon.vertx.engine.VertxAppEngine;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

public class PojoReferenceMeta implements FactoryBean<Object>, InitializingBean {

    private Class<?> mapperInterface;

    private String serverName;

    private String serverVersion;

    private int timeout;

    private Object proxy;

    private VertxAppEngine vertxAppEngine;

    private ApplicationContext applicationContext;

    public Object getProxy() {
        return getObject();
    }

    @Override
    public Object getObject() {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(mapperInterface, "mapperInterface is null");
        proxy = Invoker.createProxy(mapperInterface,serverName,serverVersion,timeout,vertxAppEngine);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
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

    public VertxAppEngine getVertxAppEngine() {
        return vertxAppEngine;
    }

    public void setVertxAppEngine(VertxAppEngine vertxAppEngine) {
        this.vertxAppEngine = vertxAppEngine;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
