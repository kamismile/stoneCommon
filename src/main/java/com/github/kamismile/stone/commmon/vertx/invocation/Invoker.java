package com.github.kamismile.stone.commmon.vertx.invocation;

import com.github.kamismile.stone.commmon.base.ResultVO;
import com.github.kamismile.stone.commmon.base.AppResponse;
import com.github.kamismile.stone.commmon.errorEnum.ErrorEnum;
import com.github.kamismile.stone.commmon.vertx.definition.InvocationType;
import com.github.kamismile.stone.commmon.vertx.engine.VertxAppEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

public class Invoker implements InvocationHandler {

    protected static final Logger logger = LogManager.getLogger(Invoker.class);


    private Class<?> mapperInterface;

    private String serverName;

    private String serverVersion;

    private int timeout;

    private VertxAppEngine vertxAppEngine;


    public Invoker(Class<?> mapperInterface, String serverName, String serverVersion, int timeout, VertxAppEngine vertxAppEngine) {
        this.mapperInterface = mapperInterface;
        this.serverName = serverName;
        this.serverVersion = serverVersion;
        this.timeout = timeout;
        this.vertxAppEngine = vertxAppEngine;
    }


    public static <T> T createProxy(Class<?> mapperInterface, String serverName, String serverVersion, int timeout, VertxAppEngine vertxAppEngine) {
        Invoker invoker = new Invoker(mapperInterface, serverName, serverVersion, timeout, vertxAppEngine);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class<?>[]{mapperInterface}, invoker);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        isValid();

        Invocation invocation = InvocationFactory.createInvocation(mapperInterface, method, InvocationType.PRODUCER);
        invocation.setServerName(serverName);
        invocation.setServerVersion(serverVersion);
        invocation.setTimeout(timeout);
        invocation.addContext("args", args);
        if (CompletableFuture.class.equals(method.getReturnType())) {
            return completableFutureInvoke(invocation);
        }

        Assert.isTrue(ResultVO.class.equals(method.getReturnType()), " returnType must be ResultVO");

        return syncInvoke(invocation);
    }

    protected CompletableFuture<Object> completableFutureInvoke(Invocation invocation) {
        CompletableFuture<Object> future = new InvocationContextCompletableFuture<Object>(invocation);
        InvokerUtils.reactiveInvoke(invocation, response -> {
            if (!response.isSuccess()) {
                logger.error("{} not found or timeout ", invocation.getVertxProviderMeta().getAddress());
                ResultVO resultVO = new ResultVO();
                resultVO.setSuccess(false);
                resultVO.setCode(ErrorEnum.NOSERVICEORTIMEOUT.getCode() + "");
                resultVO.setMessage(ErrorEnum.NOSERVICEORTIMEOUT.getMessage());
                response.setResultVO(resultVO);
            }
            Object result = response.getResultVO();
            future.complete(result);
        });
        return future;
    }

    protected Object syncInvoke(Invocation invocation) {
        AppResponse response = InvokerUtils.innerSyncInvoke(invocation);
        if (response.isSuccess()) {
            ResultVO result = response.getResultVO();
            return result;
//            if(result.getSuccess()){
//                return response.getResultVO();
//            }
//            throw  new BusinessException(Integer.parseInt(result.getCode()),result.getMessage(),result.getData());
        } else {
            logger.error("{} not found or timeout", invocation.getVertxProviderMeta().getAddress());
            ResultVO resultVO = new ResultVO();
            resultVO.setSuccess(false);
            resultVO.setCode(ErrorEnum.NOSERVICEORTIMEOUT.getCode() + "");
            resultVO.setMessage(ErrorEnum.NOSERVICEORTIMEOUT.getMessage());
            return resultVO;
        }
    }

    public void isValid() {
        this.vertxAppEngine.ensureStatusUp();
    }
}
