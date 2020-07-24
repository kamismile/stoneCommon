package io.github.kamismile.stone.commmon.vertx.engine.shard;

import io.github.kamismile.stone.commmon.base.AppRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;
import io.github.kamismile.stone.commmon.vertx.consumer.ConsumerMeta;
import io.github.kamismile.stone.commmon.vertx.definition.SimpleJsonObject;
import io.github.kamismile.stone.commmon.vertx.engine.VertxAppEngine;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultVertxShardData extends VertxShardData {

    public DefaultVertxShardData(VertxAppEngine vertxAppEngine) {
        super(vertxAppEngine);
    }


    @Override
    public void registerVertxShardData() {
        Map<String, String> userAttributes = getServiceVertxConfigProperties().getUserAttributes();

        if (CollectionUtils.isEmpty(userAttributes)) {
            scheduleTime();
            return;
        }

        String appName = userAttributes.get("appName");
        String appVersion = userAttributes.get("appVersion");
        String nodeID = getVertxAppEngine().getMgr().getNodeID();
        ConsumerMeta.add(appName, appVersion, nodeID);

        SharedData sd = getVertxAppEngine().getVertx().sharedData();

        putConsumer(appName, sd);

        scheduleTime();
    }

    private void notifySyn() {
        AppRequest<String> appRequest = new AppRequest<String>();
        appRequest.setTraceId(UUID.randomUUID().toString());
        appRequest.setpTraceId("0");
        appRequest.setData("");
        SimpleJsonObject ad = new SimpleJsonObject();
        ad.put("appRequest", JsonObject.mapFrom(appRequest));
        getVertxAppEngine().getVertx().eventBus().publish("consumer@version@syn", ad);
    }


    protected void scheduleTime() {
        // 第一个参数是任务，第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间，时间单位是毫秒
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getConsumer();
            }
        }, 0, 60000L);
    }


//    public void getConsumer(SharedData sd, List<String> nodes) {
//        sd.<String, Map<String, Long>>getClusterWideMap("consumerMap", res -> {
//            if (res.succeeded()) {
//                AsyncMap<String, Map<String, Long>> map = res.result();
//                sd.getLock("vertxShardLock", lockAsyncResult -> {
//                    if (lockAsyncResult.failed()) {
//                        return;
//                    }
//                    map.entries(entire -> {
//                        if (entire.failed()) {
//                            return;
//                        }
//                        Lock lock = lockAsyncResult.result();
//                        List<String> invalidList = ConsumerMeta.getInvalidAndReset(entire.result(), nodes);
//
//                        if (CollectionUtils.isEmpty(invalidList)) {
//                            lock.release();
//                            return;
//                        }
//
//                        ConcurrentHashMap<String, Map<String, Long>> consumerMap = ConsumerMeta.getConsumerMap();
//                        for (String server : invalidList) {
//                            Map<String, Long> mapInfo = consumerMap.get(server);
//                            if (CollectionUtils.isEmpty(mapInfo)) {
//                                map.remove(server, v -> {
//                                    if (v.failed()) {
//                                        logger.error("get error {}", ExceptionUtils.getFullStackTrace(res.cause()));
//                                    }
//                                });
//                                continue;
//                            }
//
//                            map.put(server, mapInfo, v -> {
//                                if (v.failed()) {
//                                    logger.error("get error {}", ExceptionUtils.getFullStackTrace(res.cause()));
//                                }
//                            });
//                        }
//                        lock.release();
//                    });
//
//                });
//            } else {
//                logger.error("get error {}", ExceptionUtils.getFullStackTrace(res.cause()));
//            }
//        });
//    }

    public synchronized void getConsumer() {

        SharedData sd = getVertxAppEngine().getVertx().sharedData();
        List<String> nodes = getVertxAppEngine().getMgr().getNodes();

        sd.<String, Map<String, Long>>getClusterWideMap("consumerMap", res -> {
            if (res.succeeded()) {
                AsyncMap<String, Map<String, Long>> map = res.result();
                map.entries(entire -> {
                    if (entire.failed()) {
                        return;
                    }
                    List<String> invalidList = ConsumerMeta.getInvalidAndReset(entire.result(), nodes);

                    if (CollectionUtils.isEmpty(invalidList)) {
                        return;
                    }

                    sd.getLockWithTimeout("vertxShardLock", 10000, lockAsyncResult -> {
                        if (lockAsyncResult.failed()) {
                            logger.info("lock result {}", lockAsyncResult.cause());
                            return;
                        }
                        Lock lock = lockAsyncResult.result();

                        ConcurrentHashMap<String, Map<String, Long>> consumerMap = ConsumerMeta.getConsumerMap();
                        for (String server : invalidList) {
                            Map<String, Long> mapInfo = consumerMap.get(server);
                            if (CollectionUtils.isEmpty(mapInfo)) {
                                map.remove(server, v -> {
                                    if (v.failed()) {
                                        logger.error("get error {}", ExceptionUtils.getFullStackTrace(res.cause()));
                                    }
                                });
                                continue;
                            }

                            map.put(server, mapInfo, v -> {
                                if (v.failed()) {
                                    logger.error("get error {}", ExceptionUtils.getFullStackTrace(res.cause()));
                                }
                            });
                        }
                        lock.release();
                    });
                });

            } else {
                logger.error("get error {}", ExceptionUtils.getFullStackTrace(res.cause()));
            }
        });
    }

    public void putConsumer(String appName, SharedData sd) {
        sd.<String, Map<String, Long>>getClusterWideMap("consumerMap", res -> {
            if (res.succeeded()) {
                AsyncMap<String, Map<String, Long>> map = res.result();
                Map<String, Long> infoMap = ConsumerMeta.getConsumerMap().get(appName);
                if (CollectionUtils.isEmpty(infoMap)) {
                    return;
                }

                sd.getLockWithTimeout("vertxShardLock", 10000, lockAsyncResult -> {
                    if (lockAsyncResult.failed()) {
                        logger.info("lock result {}", lockAsyncResult.cause());
                        return;
                    }
                    map.get(appName, v -> {
                        if (v.failed()) {
                            logger.error("get error {}", ExceptionUtils.getFullStackTrace(v.cause()));
                            return;
                        }
                        Lock lock = lockAsyncResult.result();
                        Map<String, Long> result = v.result();
                        if (CollectionUtils.isEmpty(result)) {
                            result = new HashMap<>();
                        }
                        result.putAll(infoMap);
                        map.put(appName, result, j -> {
                            lock.release();
                            notifySyn();
                            logger.info("shared result {}", j.succeeded());
                        });
                    });
                });

            } else {
                logger.error("register error {}", ExceptionUtils.getFullStackTrace(res.cause()));
                getVertxAppEngine().destroyForShutdownHook();
            }
        });
    }

}
