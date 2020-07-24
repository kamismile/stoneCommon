package io.github.kamismile.stone.commmon.vertx.handler.impl.provider;

import io.github.kamismile.stone.commmon.base.AppRequest;
import io.github.kamismile.stone.commmon.base.ResultVO;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.github.kamismile.stone.commmon.base.AppResponse;
import io.github.kamismile.stone.commmon.vertx.definition.BeanUtilsApplication;
import io.github.kamismile.stone.commmon.vertx.definition.InvocationType;
import io.github.kamismile.stone.commmon.vertx.definition.SimpleJsonObject;
import io.github.kamismile.stone.commmon.vertx.definition.VertxProviderMeta;
import io.github.kamismile.stone.commmon.vertx.handler.IHandler;
import io.github.kamismile.stone.commmon.vertx.invocation.Invocation;
import io.github.kamismile.stone.commmon.vertx.starter.AsyncResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class VertxProviderHandleImpl implements IHandler {

    @Override
    public InvocationType getInvocationType() {
        return InvocationType.PRODUCER;
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public void handle(Invocation invocation, AsyncResponse asyncResp) throws Exception {
        Vertx vertx = BeanUtilsApplication.getApplicationContext().getBean(Vertx.class);

        VertxProviderMeta vertxProviderMeta = invocation.getVertxProviderMeta();

        Object[] args = (Object[]) invocation.getContext("args");
        Assert.notNull(args, "method must have AppRequest arg");
        List<Object> list = Arrays.asList(args);
        AppRequest appRequest = (AppRequest) list.stream().filter(v -> v.getClass().equals(AppRequest.class)).findFirst().get();
        appRequest.setpTraceId(appRequest.getTraceId());
        if (StringUtils.isBlank(appRequest.getpTraceId())) {
            appRequest.setpTraceId("0");
        }
        appRequest.setTraceId(UUID.randomUUID().toString());

        SimpleJsonObject ad = new SimpleJsonObject();
        ad.put("appRequest", appRequest);

        DeliveryOptions deliveryOptions=new DeliveryOptions();
        if(invocation.getTimeout() > 0){
            deliveryOptions.setSendTimeout(invocation.getTimeout());
        }

        vertx.eventBus().request(vertxProviderMeta.getAddress(), ad,deliveryOptions, v -> {
            AppResponse appResponse = new AppResponse();
            appResponse.setSuccess(v.succeeded());
            if (!Objects.isNull(v.result()) && !Objects.isNull(v.result().body())) {
                JsonObject jsonObject = ((JsonObject) v.result().body()).getJsonObject("resultVO");
                ResultVO response = jsonObject.mapTo(ResultVO.class);
                appResponse.setResultVO(response);
            }
            asyncResp.handle(appResponse);
        });
    }
}
