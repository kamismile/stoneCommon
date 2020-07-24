/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.github.kamismile.stone.commmon.vertx.consumer;

import io.github.kamismile.stone.commmon.vertx.definition.ConsumerVersion;
import io.github.kamismile.stone.commmon.vertx.definition.VertxProviderMeta;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author dong.li
 * @version $Id: ConsumerMeta, v 0.1 2020/4/2 15:51 dong.li Exp $
 */
public class ConsumerMeta {

    //服务    nodeID,版本
    private static ConcurrentHashMap<String, Map<String, Long>> consumerMap = new ConcurrentHashMap<String, Map<String, Long>>();


    public static void add(String serverName, String serverVersion, String nodeID) {

        Map<String, Long> map = consumerMap.get(serverName);
        if (CollectionUtils.isEmpty(map)) {
            map = new HashMap<String, Long>();
        }

        ConsumerVersion consumerVersion = new ConsumerVersion(serverVersion);

        map.put(nodeID, consumerVersion.getNumberVersion());
        consumerMap.put(serverName, map);

    }


    public static String findVersion(VertxProviderMeta vertxProviderMeta) {
        String serverName = vertxProviderMeta.getServerName();
        String serverVersion = vertxProviderMeta.getServerVersion();

        Map<String, Long> map = consumerMap.get(serverName);

        if (CollectionUtils.isEmpty(map)) {
            return "";
        }

        ConsumerVersion consumerVersion = new ConsumerVersion(serverVersion);

        long numberVersion = consumerVersion.getNumberVersion();
        Collection<Long> versions = map.values().stream().distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        if (versions.contains(numberVersion)) {
            return new ConsumerVersion(numberVersion).getVersion();
        }

        //永远最新
        if (numberVersion == 0) {
            return new ConsumerVersion(versions.stream().findFirst().get()).getVersion();
        }

        Long aroundNumberVersion = versions.stream().map(v -> (Math.abs(v - numberVersion))).sorted().findFirst().get();

        if (versions.contains(aroundNumberVersion + numberVersion)) {
            return new ConsumerVersion(aroundNumberVersion + numberVersion).getVersion();
        }
        return new ConsumerVersion(numberVersion - aroundNumberVersion).getVersion();
    }


    public static ConcurrentHashMap<String, Map<String, Long>> getConsumerMap() {
        return consumerMap;
    }


    public static List<String> getInvalidAndReset(Map<String, Map<String, Long>> maps, List<String> nodes) {
        HashMap<String, Map<String, Long>> liveMap = new HashMap<String, Map<String, Long>>();
        List<String> invalidList = new ArrayList<>();

        if (CollectionUtils.isEmpty(maps)) {
            return invalidList;
        }

        maps.keySet().forEach(v -> {
            Map<String, Long> map = maps.get(v);
            if (CollectionUtils.isEmpty(map)) {
                return;
            }
            map.keySet().forEach(k -> {
                Map<String, Long> info = new HashMap<>();
                info.put(k, map.get(k));
                if (nodes.contains(k)) {
                    if(MapUtils.isEmpty(liveMap.get(v))){
                        liveMap.put(v, info);
                    }

                    Map<String, Long> serverInfo = liveMap.get(v);
                    serverInfo.put(k, map.get(k));

                } else {
                    invalidList.add(v);
                }
            });
        });

        consumerMap.clear();
        consumerMap.putAll(liveMap);
        return invalidList.stream().distinct().collect(Collectors.toList());
    }

}
