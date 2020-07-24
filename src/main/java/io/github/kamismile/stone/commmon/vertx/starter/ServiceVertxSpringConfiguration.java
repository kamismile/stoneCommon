/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.github.kamismile.stone.commmon.vertx.starter;

import io.vertx.core.Vertx;
import io.github.kamismile.stone.commmon.vertx.ServiceVertxConfigProperties;
import io.github.kamismile.stone.commmon.vertx.definition.BeanUtilsApplication;
import io.github.kamismile.stone.commmon.vertx.engine.VertxAppEngine;
import io.github.kamismile.stone.commmon.vertx.engine.shard.DefaultVertxShardData;
import io.github.kamismile.stone.commmon.vertx.engine.shard.VertxShardData;
import io.github.kamismile.stone.commmon.vertx.exception.DefaultErrorVertxExceptionHandler;
import io.github.kamismile.stone.commmon.vertx.exception.VertxExException;
import io.github.kamismile.stone.commmon.vertx.provider.RpcReferenceProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author dong.li
 * @version $Id: ServiceVertxSpringConfiguration, v 0.1 2020/3/25 16:05 dong.li Exp $
 */
@Configuration
@EnableConfigurationProperties(ServiceVertxConfigProperties.class)
@ComponentScan(basePackages = "io.github.kamismile.stone.commmon.vertx")
public class ServiceVertxSpringConfiguration {

    @Bean
    @ConditionalOnClass(VertxExException.class)
    @ConditionalOnMissingBean(VertxExException.class)
    DefaultErrorVertxExceptionHandler defaultErrorVertxExceptionHandler() {
        return new DefaultErrorVertxExceptionHandler();
    }

    @Bean
//    @ConditionalOnMissingClass(value = {"org.thearchy.stoneEureka.client.service.help.IClientSyncService"})
    @ConditionalOnMissingBean(VertxShardData.class)
    VertxShardData vertxShardData(VertxAppEngine vertxAppEngine){
        return new DefaultVertxShardData(vertxAppEngine);
    }


    @Bean
    VertxAppEngine vertxAppEngine() {
        return new VertxAppEngine();
    }

//    @Bean
//    SpringVerticleFactory springVerticleFactory() {
//        return new SpringVerticleFactory();
//    }

    /**
     * Exposes the clustered Vert.x instance.
     * We must disable destroy method inference, otherwise Spring will call the {@link Vertx#close()} automatically.
     */
    @Bean(destroyMethod = "")
    Vertx vertx(VertxAppEngine vertxAppEngine) {
        return vertxAppEngine.getVertx();
    }

//    @Bean
//    @ConditionalOnMissingClass(value = {"org.thearchy.stoneEureka.client.service.help.IClientSyncService"})
//    @ConditionalOnMissingBean(MembershipListener.class)
//    VertxMembershipListener vertxMembershipListener() {
//        return new VertxMembershipListener();
//    }


    @Bean
    RpcReferenceProcessor rpcReferenceProcessor(ServiceVertxConfigProperties serviceVertxConfigProperties,
                                                VertxAppEngine vertxAppEngine,
                                                ApplicationContext applicationContext) {
        return new RpcReferenceProcessor(serviceVertxConfigProperties, vertxAppEngine, applicationContext);
    }


//    @Bean
//    VertxProviderHandleImpl vertxProviderHandle() {
//        return new VertxProviderHandleImpl();
//    }
//
//
//    @Bean
//    ProviderVersionChooseHandleImpl providerVersionChooseHandle() {
//        return new ProviderVersionChooseHandleImpl();
//    }

//    @Bean
//    VertxShardData vertxShardData(VertxAppEngine vertxAppEngine){
//        return new VertxShardData(vertxAppEngine);
//    }

    @Bean
    BeanUtilsApplication beanUtilsApplication() {
        return new BeanUtilsApplication();
    }
}
