package io.github.kamismile.stone.commmon.vertx.provider;

import io.github.kamismile.stone.commmon.vertx.ServiceVertxConfigProperties;
import io.github.kamismile.stone.commmon.vertx.engine.VertxAppEngine;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

public class RpcReferenceProcessor implements BeanPostProcessor, EmbeddedValueResolverAware {

    private StringValueResolver resolver;

    private VertxAppEngine vertxAppEngine;

    private ServiceVertxConfigProperties serviceVertxConfigProperties;

    private ApplicationContext applicationContext;

    public RpcReferenceProcessor(ServiceVertxConfigProperties serviceVertxConfigProperties, VertxAppEngine vertxAppEngine, ApplicationContext applicationContext) {
        this.serviceVertxConfigProperties = serviceVertxConfigProperties;
        this.applicationContext = applicationContext;
        this.vertxAppEngine = vertxAppEngine;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 扫描所有field，处理扩展的field标注
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                processConsumerField(bean, field);
            }
        });

        return bean;
    }


    protected void processConsumerField(Object bean, Field field) {
        VertxReference reference = field.getAnnotation(VertxReference.class);
        if (reference == null) {
            return;
        }

        if (!field.getType().isInterface()) {
            Assert.isTrue(true, "VertxReference must interface ");
            return;
        }

        handleReferenceField(bean, field, reference);
    }

    private void handleReferenceField(Object bean, Field field, VertxReference reference) {
        Class<?> interfaceType = field.getType();
        Map<String, ServiceVertxConfigProperties.VertxServerProvider> servers = this.serviceVertxConfigProperties.getServers();
        Assert.notEmpty(servers, "servers is empty");

        ServiceVertxConfigProperties.VertxServerProvider currentProvider = null;
        for (ServiceVertxConfigProperties.VertxServerProvider provider : servers.values()) {
            Assert.notEmpty(provider.getBasePackages(), "basePackages is empty");
            boolean isEmpty = provider.getBasePackages().stream().filter(v -> interfaceType.getPackage().getName().contains(v)).collect(Collectors.toList()).isEmpty();
            if (!isEmpty) {
                currentProvider = provider;
                break;
            }
        }

        Assert.notNull(currentProvider, "servers is empty");

        PojoReferenceMeta pojoReference = new PojoReferenceMeta();
        pojoReference.setMapperInterface(interfaceType);
        pojoReference.setTimeout(currentProvider.getTimeout());
        pojoReference.setServerName(currentProvider.getServerName());
        pojoReference.setServerVersion(currentProvider.getServerVersion());
        pojoReference.setApplicationContext(applicationContext);
        pojoReference.setVertxAppEngine(vertxAppEngine);

        if (reference.timeout() >= 0) {
            pojoReference.setTimeout(reference.timeout());
        }

        pojoReference.afterPropertiesSet();

        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, pojoReference.getProxy());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
