package com.github.kamismile.stone.commmon.dao;

import com.github.kamismile.stone.commmon.component.RedisClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

/**
 * Created by lidong on 2017/3/7.
 */
public class CommonCacheDao {

//    @Autowired
    protected CacheManager cacheManager;

//    @Autowired
    protected RedisClient redisClient;

    public  Cache.ValueWrapper getCache(String cacheName,String valueName){
        Cache cache = cacheManager.getCache(cacheName);
        Cache.ValueWrapper valueWrapper = cache.get(valueName);
        return valueWrapper;
    }

    public  Cache.ValueWrapper getCache(String cacheNames) {
        String[] cacheName = cacheNames.split(":");
        String valueName = StringUtils.join(cacheName,":",1,cacheName.length);
        return getCache(cacheName[0],valueName);
    }


    public  void expireList(List<String> keys, long liveTime){
        redisClient.expireList(keys, liveTime);
    }
}
