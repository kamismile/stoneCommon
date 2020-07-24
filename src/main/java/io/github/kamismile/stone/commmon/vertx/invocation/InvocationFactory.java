package io.github.kamismile.stone.commmon.vertx.invocation;

import io.github.kamismile.stone.commmon.vertx.consumer.VertxAddress;
import io.github.kamismile.stone.commmon.vertx.definition.BeanUtilsApplication;
import io.github.kamismile.stone.commmon.vertx.definition.InvocationType;
import io.github.kamismile.stone.commmon.vertx.handler.IHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InvocationFactory {
    private InvocationFactory() {
    }


    public static Invocation createInvocation(Class<?> mapperInterface, Method method, InvocationType invocationType) {
        Map<String, IHandler> iHandlerMap = BeanUtilsApplication.getApplicationContext().getBeansOfType(IHandler.class);

        List<IHandler> iHandlerList = iHandlerMap.values().stream().filter(v -> v.getInvocationType().equals(invocationType)).sorted(Comparator.comparing(IHandler::order)).collect(Collectors.toList());

        Assert.notEmpty(iHandlerList, invocationType.name() + " is empty");

        Invocation invocation = new Invocation();
        invocation.setIHandlerList(iHandlerList);

        method.setAccessible(true);
        VertxAddress vd = AnnotationUtils.findAnnotation(method, VertxAddress.class);
        Assert.notNull(vd, " must have VertxAddress");

        StringBuilder address = new StringBuilder();
        address.append(vd.value());
        if (StringUtils.isBlank(vd.value())) {
            address.append(mapperInterface.getName()).append("$").append(method.getName());
        }

        invocation.setServerAddress(address.toString());
        return invocation;
    }
}
