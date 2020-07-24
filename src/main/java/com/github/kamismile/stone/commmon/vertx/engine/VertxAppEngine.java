package com.github.kamismile.stone.commmon.vertx.engine;

import com.github.kamismile.stone.commmon.vertx.engine.shard.DefaultVertxShardData;
import com.github.kamismile.stone.commmon.vertx.engine.shard.VertxShardData;
import com.hazelcast.config.*;
import com.hazelcast.core.*;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import com.github.kamismile.stone.commmon.exception.BusinessException;
import com.github.kamismile.stone.commmon.vertx.ServiceVertxConfigProperties;
import com.github.kamismile.stone.commmon.vertx.consumer.BaseControl;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class VertxAppEngine {
    protected final Logger logger = LogManager.getLogger(VertxAppEngine.class);

    private VertxAppEnum status = VertxAppEnum.DOWN;

    private Vertx vertx;

    private HazelcastClusterManager mgr;

    private HazelcastInstance hazelcastInstance;

    private Cluster cluster;

    @Autowired
    ServiceVertxConfigProperties serviceVertxConfigProperties;

    //    @Autowired
//    SpringVerticleFactory springVerticleFactory;

    @Autowired
    VertxShardData vertxShardData;

    @PostConstruct
    public synchronized void init() {
        if (VertxAppEnum.DOWN.equals(status)) {
            try {
                doRun();
            } catch (Exception e) {
                logger.error(MessageFormat.format("appEngine close:{0} ", ExceptionUtils.getStackTrace(e)));
                status = VertxAppEnum.FAILED;
                throw new IllegalStateException("VertxAppEngine init failed.", e);
            }
        }
    }

    private synchronized void doRun() throws Exception {
        status = VertxAppEnum.STARTING;
        Config hazelcastConfig = new Config();
        GroupConfig group = new GroupConfig();
        group.setName(serviceVertxConfigProperties.getName());
        group.setPassword(serviceVertxConfigProperties.getPassword());
        hazelcastConfig.setGroupConfig(group);// 加入组的配置，防止广播环境下，负载串到别的开发机中
        hazelcastConfig.setProperty("hazelcast.shutdownhook.enabled", "false");
        hazelcastConfig.addSemaphoreConfig(getSemaphoreConfig());
        hazelcastConfig.addSemaphoreConfig(getSemaphoreConfigStartupManager());
        JoinConfig join = hazelcastConfig.getNetworkConfig().getJoin();
        TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
//        serviceVertxConfigProperties.getMembers().stream().forEach(v -> tcpIpConfig.addMember(v));
        tcpIpConfig.setMembers(serviceVertxConfigProperties.getMembers());
        tcpIpConfig.setEnabled(true);
        join.getMulticastConfig().setEnabled(false);
        join.getAwsConfig().setEnabled(false);

        mgr = new HazelcastClusterManager(hazelcastConfig);
        EventBusOptions eventBusOptions = new EventBusOptions();
        eventBusOptions.setClustered(true);
        eventBusOptions.setHost(serviceVertxConfigProperties.getHost());
        eventBusOptions.setPort(serviceVertxConfigProperties.getPort());
        eventBusOptions.setClusterPublicHost(serviceVertxConfigProperties.getClusterPublicHost());
        VertxOptions options = new VertxOptions();
        options.setWorkerPoolSize(serviceVertxConfigProperties.getWorkers())
                .setClusterManager(mgr)
                .setEventBusOptions(eventBusOptions)
                .setHAEnabled(true);

        CompletableFuture<Vertx> future = new CompletableFuture<>();
        Vertx.clusteredVertx(options, ar -> {
            if (ar.succeeded()) {
                future.complete(ar.result());
            } else {
                future.completeExceptionally(ar.cause());
            }
        });
        vertx = future.get();
        vertx.exceptionHandler(v -> {
            logger.error("error {}", ExceptionUtils.getStackTrace(v));
        });
        processVertx();

        status = VertxAppEnum.UP;

//        Thread shutdownHook = new Thread(this::destroyForShutdownHook);
//        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private void processVertx() {
//        vertx.registerVerticleFactory(springVerticleFactory);
        hazelcastInstance = mgr.getHazelcastInstance();
//        HealthChecks hc = HealthChecks.create(vertx);
//        hc.register("stone-procedure", 15000, future -> future.complete(Status.OK()));
        cluster = hazelcastInstance.getCluster();
        if (vertxShardData instanceof DefaultVertxShardData) {
            MembershipListener membershipListener = new VertxMembershipListener((DefaultVertxShardData) vertxShardData, this);
            cluster.addMembershipListener(membershipListener);
        }
    }

    public static SemaphoreConfig getSemaphoreConfig() {
        SemaphoreConfig semaphoreConfig = new SemaphoreConfig();
        semaphoreConfig.setName("__vertx.*");
        semaphoreConfig.setInitialPermits(1);
        return semaphoreConfig;
    }

    public static SemaphoreConfig getSemaphoreConfigStartupManager() {
        SemaphoreConfig semaphoreConfig = new SemaphoreConfig();
        semaphoreConfig.setName("startupManager");
        semaphoreConfig.setInitialPermits(1);
        return semaphoreConfig;
    }

    @PreDestroy
    public void destroyForShutdownHook() {
        if (status == VertxAppEnum.DOWN) {
            return;
        }

        status = VertxAppEnum.DOWN;

        CompletableFuture<Void> future = new CompletableFuture<>();
        vertx.close(ar -> future.complete(null));
        try {
            future.get();
        } catch (Exception e) {
            logger.error("app destory error{}", ExceptionUtils.getStackTrace(e));
        }
    }

    public HazelcastClusterManager getMgr() {
        return mgr;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public Cluster getCluster() {
        return cluster;
    }

    @EventListener
    public void deployVerticles(ApplicationReadyEvent event) {
        Map<String, BaseControl> map = event.getApplicationContext().getBeansOfType(BaseControl.class);
        DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setHa(true)
//                .setInstances(serviceVertxConfigProperties.getInstances())
                .setWorker(true)
                .setWorkerPoolName("vertx-work-pool")
                .setWorkerPoolSize(serviceVertxConfigProperties.getDevWorkers())
                .setMultiThreaded(true);
        for (BaseControl baseControl : map.values()) {
//            vertx.deployVerticle(springVerticleFactory.prefix() + ":" + baseControl.getClass().getName(), deploymentOptions);
//            vertx.deployVerticle( baseControl.getClass().getName(), deploymentOptions);
            vertx.deployVerticle(baseControl, deploymentOptions);
        }

        vertxShardData.registerVertxShardData();
    }

    public VertxAppEnum getStatus() {
        return status;
    }


    public void ensureStatusUp() {
        VertxAppEnum currentStatus = getStatus();
        if (!VertxAppEnum.UP.equals(currentStatus)) {
            String message = "The request is rejected. Cannot process the request due to STATUS = " + currentStatus;
            logger.warn(message);
            throw new BusinessException(-1, message);
        }
    }

}
