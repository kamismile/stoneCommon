package com.github.kamismile.stone.commmon.component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.cache.Cache;
import org.springframework.cache.jcache.JCacheCacheManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lidong on 2017/5/27.
 */
public class ClientCache {
    public static final  LoadingCache<String, RateLimiter> client =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(6, TimeUnit.MINUTES)
                    .build(new CacheLoader<String, RateLimiter>() {
                        @Override
                        public RateLimiter load(String key) throws Exception {
                            return RateLimiter.create(0.003);
                        }
                    });

    public static Cache getClientCache(JCacheCacheManager jCacheCacheManager){
        return    jCacheCacheManager.getCache("clientCache");
    }
}
