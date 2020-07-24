package com.github.kamismile.stone.commmon.aop;

import com.github.kamismile.stone.commmon.component.DistributedRedisLock;
import com.github.kamismile.stone.commmon.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by lidong on 2017/2/21.
 */

//@Aspect
//@Component
public class DistributedRedisLockService {


    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Around("@annotation(com.github.kamismile.stone.commmon.component.DistributedRedisLock)")
    public void distributedRedisLock(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        DistributedRedisLock distributedRedisLock = method.getAnnotation(DistributedRedisLock.class);
        Lock lock = redisLockRegistry.obtain(distributedRedisLock.obtain());
        boolean res = lock.tryLock(distributedRedisLock.timeOut(), TimeUnit.SECONDS);
        if(!res){
            throw new BusinessException(distributedRedisLock.code(),distributedRedisLock.message());
        }
        try{
            pjp.proceed();
        }finally {
            lock.unlock();
        }
    }


}
