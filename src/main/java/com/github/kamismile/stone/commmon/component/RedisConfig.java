package com.github.kamismile.stone.commmon.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lidong on 2015/12/4.
 */
//@Configuration
//@EnableCaching(proxyTargetClass = true)
public class RedisConfig extends CachingConfigurerSupport {
    protected final Logger log = LogManager.getLogger(getClass());

    @Autowired
    @Qualifier("stringRedisTemplate")
    RedisTemplate<String,String> redisTemplate;

//    @Bean
//    public CompositeCacheManager cacheManager(
//            JCacheCacheManager jCacheCacheManager,
//            RedisCacheManager redisCacheManager) {
//        CompositeCacheManager cacheManager = new CompositeCacheManager();
//        List<CacheManager> managers = new ArrayList<CacheManager>();
//        managers.add(jCacheCacheManager);
//        managers.add(redisCacheManager);
//        cacheManager.setCacheManagers(managers);
//        cacheManager.setFallbackToNoOpCache(true);
//        return cacheManager;
//    }

    @Bean
    @Primary
    public RedisCacheManager cacheManager(){
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        cacheConfig.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        cacheConfig.entryTtl(Duration.ofMinutes(10));

        RedisCacheConfiguration cacheWeek = RedisCacheConfiguration.defaultCacheConfig();
        cacheWeek.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        cacheWeek.entryTtl(Duration.ofDays(7));

        RedisCacheConfiguration cacheDay = RedisCacheConfiguration.defaultCacheConfig();
        cacheDay.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        cacheDay.entryTtl(Duration.ofDays(1));

        RedisCacheConfiguration cacheShort = RedisCacheConfiguration.defaultCacheConfig();
        cacheShort.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        cacheShort.entryTtl(Duration.ofMinutes(6));

        Map<String, RedisCacheConfiguration> cacheConfigurations=new HashMap<String, RedisCacheConfiguration>();
        cacheConfigurations.put("cacheWeek",cacheWeek);
        cacheConfigurations.put("cacheDay",cacheDay);
        cacheConfigurations.put("cacheShort",cacheShort);

        RedisCacheManager cacheManager = RedisCacheManager.builder(redisTemplate.getConnectionFactory())
                .cacheDefaults(cacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
//todo check
//        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//
//        Map<String, Long> expires = new HashMap<String, Long>();
//        expires.put("cacheWeek", new Long(604800));//24*60*60*7
//        expires.put("cacheDay", new Long(86400));//24*60*60
//        expires.put("cacheShort", new Long(600));//2*60
//        cacheManager.setDefaultExpiration(new Long(10));
//        cacheManager.setExpires(expires);
//        cacheManager.setUsePrefix(true);//
//        cacheManager.setDefaultExpiration(30);
//        return cacheManager;
        return cacheManager;
    }





    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.error(String.format("%s:%s:%s", cache.getName(), key, exception.getMessage()),  exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key,
                                            Object value) {
                log.error(String.format("%s:%s:%s", cache.getName(), key, exception.getMessage()),  exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error(String.format("%s:%s:%s", cache.getName(), key, exception.getMessage()),   exception);
                log.error(exception.getMessage(), exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.error(String.format("%s:%s", cache.getName(), exception.getMessage()), exception);
                log.error(exception.getMessage(), exception);
            }
        };
    }

}
