package com.github.kamismile.stone.commmon.component;

import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.*;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

/**
 * Created by lidong on 2017/7/10.
 */
//@Configuration
public class EhcacheConfig {

    @Value("${spring.ehcacheConfig.file:-1}")
    Resource ehcacheConfig;

    @Bean
    public JCacheCacheManager jcacheCacheManager() throws Exception {
        JCacheCacheManager cm = new JCacheCacheManager();
        if("-1".equals(ehcacheConfig.getURL().getPath())){
            cm.setCacheManager(jsr107cacheManager());
        }else{
            cm.setCacheManager(jsr107cacheManagerConfig());
        }

        return cm;
    }

    public CacheManager jsr107cacheManagerConfig() throws Exception{
        CachingProvider provider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        CacheManager cacheManager = provider.getCacheManager(
                ehcacheConfig.getURI(),
                Thread.currentThread().getContextClassLoader());
            return cacheManager;
    }

    public CacheManager jsr107cacheManager() throws Exception{
        CachingProvider provider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        CacheManager cacheManager = provider.getCacheManager();
//        MutableConfiguration<String, String> configuration =
//                new MutableConfiguration<String, String>()
//                        // Cannot set type for store! this may be a bug in spring or ehCache
//                        //.setTypes(Long.class, String.class)
//                        .setStoreByValue(false)
//                        .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.FIVE_MINUTES));
        return cacheManager;
    }

}
