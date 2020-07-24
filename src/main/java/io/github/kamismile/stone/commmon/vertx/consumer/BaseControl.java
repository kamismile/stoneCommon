package io.github.kamismile.stone.commmon.vertx.consumer;

import io.github.kamismile.stone.commmon.base.AppRequest;
import io.github.kamismile.stone.commmon.base.AppResponse;
import io.github.kamismile.stone.commmon.base.ResultVO;
import io.github.kamismile.stone.commmon.base.pagination.PaginationData;
import io.github.kamismile.stone.commmon.util.JsonUtil;
import io.github.kamismile.stone.commmon.util.ValueUtils;
import io.github.kamismile.stone.commmon.vertx.ServiceVertxConfigProperties;
import io.github.kamismile.stone.commmon.vertx.definition.SimpleJsonObject;
import io.github.kamismile.stone.commmon.vertx.definition.VertxMeta;
import io.github.kamismile.stone.commmon.vertx.engine.VertxAppEngine;
import io.github.kamismile.stone.commmon.vertx.exception.VertxExException;
import io.github.kamismile.stone.commmon.vertx.executor.ExecutorManager;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by lidong on 2017/2/20.
 */
public abstract class BaseControl extends AbstractVerticle {
    protected final Logger logger = LogManager.getLogger("bLog");

    @Autowired
    private VertxExException vertxExException;

    @Autowired
    private ServiceVertxConfigProperties serviceVertxConfigProperties;

    @Autowired
    private VertxAppEngine vertxAppEngine;

    private String appName;
    private String appVersion;


    public Map<String, Object> getMsgJson(Message msg) {
        return JsonUtil.json2Map(ValueUtils.isStringNull(msg.body()));
    }

//    public Map<String,String> getMsgJsonString(Message msg){
//        return   JsonUtil.json2MapString(ValueUtils.isStringNull(msg.body()));
//    }

    @Override
    public void start() throws Exception {
//        super.start();
        checkMetaInfo();

        Object current = null;
        try {
            current = AopContext.currentProxy();
        } catch (IllegalStateException e) {
            current = this;
        }

//        Class<?> ultimateTargetClass = AopProxyUtils.ultimateTargetClass(current);
        Class<?> ultimateTargetClass = AopProxyUtils.proxiedUserInterfaces(current)[0];
        Method[] methods = ultimateTargetClass.getMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            VertxAddress vd = AnnotationUtils.findAnnotation(method, VertxAddress.class);
            if (ValueUtils.isNull(vd)) {
                continue;
            }

            Assert.isTrue(ResultVO.class.equals(method.getReturnType()) || CompletableFuture.class.equals(method.getReturnType()), " returnType must be ResultVO or CompletableFuture<ResultVO>");

            Assert.isTrue(method.getGenericParameterTypes().length == 1, "method must have only one arg");

            Assert.isTrue(method.getParameterTypes()[0].equals(AppRequest.class), "arg must be AppRequest");

            StringBuilder address = new StringBuilder();
            address.append(vd.value());
            if (StringUtils.isBlank(vd.value())) {
                address.append(ultimateTargetClass.getName()).append("$").append(method.getName());
            }

            VertxMeta vertxMeta = new VertxMeta(appName, address.toString(), appVersion);

            vertxMeta.setNodeID(vertxAppEngine.getMgr().getNodeID());

            if (vd.isRouter() == 1) {
                vertxMeta.setExInfo("@router@" + vertxMeta.getNodeID());
            }

            String vertxAddress = vertxMeta.getAddress();

            if (vd.isPrefix() == 0) {
                vertxAddress = vd.value() + vertxMeta.getExInfo();
            }

            Object finalCurrent = current;


            vertx.eventBus().consumer(vertxAddress, v -> {
                consumerMsg(method, vertxMeta, finalCurrent, v);
            });
        }

    }

    private void consumerMsg(Method method, VertxMeta vertxMeta, Object finalCurrent, Message<Object> v) {
//        new Fiber(ExecutorManager.getFiberForkJoinScheduler(),() -> {
        ExecutorManager.findExecutor(vertxMeta.getAddress()).execute(() -> {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            try {
                JsonObject jsonObject = (JsonObject) v.body();
                AppRequest appRequest = JsonUtil.fromJson(jsonObject.getJsonObject("appRequest").toString(), method.getGenericParameterTypes()[0]);
//                AppRequest appRequest = (AppRequest) jsonObject.getValue("appRequest");
                appRequest.setpTraceId(appRequest.getTraceId());
                appRequest.setTraceId(UUID.randomUUID().toString());
                logger.info("{\"address\":{},\"request\":{}}", v.address(), v.body());
                Object result = method.invoke(finalCurrent, appRequest);
                if (result instanceof Void) {
                    return;
                } else if (result instanceof ResultVO) {
                    ResultVO resultVO = ((ResultVO) result);
                    SimpleJsonObject simpleJsonObject = new SimpleJsonObject();
                    simpleJsonObject.put("resultVO", resultVO);
                    v.reply(simpleJsonObject);
                } else if (result instanceof AppResponse) {
                    AppResponse appResponse = ((AppResponse) result);
                    SimpleJsonObject simpleJsonObject = new SimpleJsonObject();
                    simpleJsonObject.put("appResponse", appResponse);
                    v.reply(simpleJsonObject);
                } else if (result instanceof CompletableFuture) {
                    Object resultVO = ((CompletableFuture) result).join();
                    Assert.isInstanceOf(ResultVO.class, resultVO, " returnType must be ResultVO or CompletableFuture<ResultVO>");
                    SimpleJsonObject simpleJsonObject = new SimpleJsonObject();
                    simpleJsonObject.put("resultVO", resultVO);
                    v.reply(simpleJsonObject);
                }
            } catch (Exception e) {
                vertxExException.handle(vertxMeta.getAddress(), v, e);
            } finally {
                stopWatch.stop();
                if (stopWatch.getLastTaskTimeMillis() > 1000) {
                    logger.info("{\"address\":{},\"请求时间time\":{},\"request\":{}}", v.address(), stopWatch.getLastTaskTimeMillis(), v.body());
                }
            }
        });
//    }).start();
    }


    private void checkMetaInfo() {
        Assert.notNull(serviceVertxConfigProperties, "serviceVertxConfigProperties is null");
        Map<String, String> userAttributes = serviceVertxConfigProperties.getUserAttributes();
        Assert.notNull(userAttributes, "userAttributes is null");
        appName = userAttributes.get("appName");
        appVersion = userAttributes.get("appVersion");
        Assert.hasText(appName, "appName is null");
        Assert.hasText(appVersion, "appVersion is null");
    }


    protected ResultVO getResultVO() {
        return getResultVO("0");
    }

    protected ResultVO getResultVO(String code) {
        return getResultVO(code, "");
    }

    protected ResultVO getResultVO(String code, String msg) {
        ResultVO vo = null;
        try {
            vo = getResultVO(code, msg, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return vo;
    }

    protected <T> ResultVO<T> successfulResultVO(T result) {
        ResultVO vo = null;
        try {
            vo = getResultVO("0", "操作成功", result);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return vo;
    }

    protected ResultVO failureResultVO(String message) {
        ResultVO vo = null;
        try {
            vo = getResultVO("-1", message, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return vo;
    }


    protected <T> ResultVO getResultVO(String code, String msg, T obj) throws IllegalAccessException {
        final ResultVO<T> transfer = new ResultVO<T>(code, msg, obj);
        if (obj instanceof PaginationData) {
            PaginationData paginationData = (PaginationData) obj;
            transfer.setPage(paginationData.getPageInfo());
            transfer.setData((T) paginationData.getData());
        }
        return transfer;
    }


    public VertxAppEngine getVertxAppEngine() {
        return vertxAppEngine;
    }
}
