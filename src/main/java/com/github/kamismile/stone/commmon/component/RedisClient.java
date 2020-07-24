package com.github.kamismile.stone.commmon.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lidong on 2017/2/23.
 */
//@Component
public class RedisClient {

    @Autowired
    @Qualifier("stringRedisTemplate")
    RedisTemplate<String, ?> redisTemplate;

    public RedisTemplate<String, ?> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 对key设置过期时间
     *
     * @param key      键
     * @param liveTime 过期时间
     * @return
     */
    public Boolean expire(final String key, final long liveTime) {
        RedisSerializer serializer = redisTemplate.getStringSerializer();
        return redisTemplate.execute((RedisCallback<Boolean>) (c) -> c.expire(serializer.serialize(key), liveTime));
    }

    public void expireList(List<String> keys, long liveTime) {
        RedisSerializer serializer = redisTemplate.getStringSerializer();
        redisTemplate.execute((RedisCallback<Boolean>) (c) -> {
            keys.stream().forEach(key -> c.expire(serializer.serialize(key), liveTime));
            return true;
        }, false, true);
    }


    public Boolean setNX(final String key, final String value) {
        RedisSerializer serializer = redisTemplate.getStringSerializer();
        byte[] serializerKey = serializer.serialize(key);
        byte[] serializerValue = serializer.serialize(value);
        return redisTemplate.execute((RedisCallback<Boolean>) (c) -> c.setNX(serializerKey, serializerValue));
    }

    public Boolean set(final String key, final String value) {
        RedisSerializer serializer = redisTemplate.getStringSerializer();
        byte[] serializerKey = serializer.serialize(key);
        byte[] serializerValue = serializer.serialize(value);
        return redisTemplate.execute((RedisCallback<Boolean>) (c) -> c.set(serializerKey, serializerValue));
    }

    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean setEx(final String key, long seconds, final String value) {
        RedisSerializer serializer = redisTemplate.getStringSerializer();
        byte[] serializerKey = serializer.serialize(key);
        byte[] serializerValue = serializer.serialize(value);
        return redisTemplate.execute((RedisCallback<Boolean>) (c) -> c.setEx(serializerKey, seconds, serializerValue));
    }

    public Long delKey(final String... keys) {
        return redisTemplate.execute((RedisCallback<Long>) (c) -> c.del(rawKeys(keys)));
    }

    public Long delHashKey(final String keys, final String... fields) {
        return redisTemplate.execute((RedisCallback<Long>) (c) -> c.hDel(rawKey(keys), rawKeys(fields)));
    }

    public void putHash(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public void putHashValue(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getHashValue(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public Object getBoundHashValue(String key, Object hashKey) {
        return redisTemplate.boundHashOps(key).get(hashKey);
    }

    public Map<Object, Object> getHashAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    public Set<Object> getSetAll(String key) {
        return (Set<Object>) redisTemplate.opsForSet().members(key);
    }

    private byte[][] rawKeys(final String... keys) {
        final byte[][] rawKeys = new byte[keys.length][];

        int i = 0;
        for (String key : keys) {
            rawKeys[i++] = rawKey(key);
        }

        return rawKeys;
    }

    private byte[] rawKey(Object key) {
        RedisSerializer keySerializer = redisTemplate.getStringSerializer();
        Assert.notNull(key, "non null key required");
        if (keySerializer == null && key instanceof byte[]) {
            return (byte[]) key;
        }
        return keySerializer.serialize(key);
    }
}
