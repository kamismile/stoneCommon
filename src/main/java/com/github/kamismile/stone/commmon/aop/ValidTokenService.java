package com.github.kamismile.stone.commmon.aop;

import com.github.kamismile.stone.commmon.vertx.consumer.VertxAddress;
import com.github.kamismile.stone.commmon.dao.UserCacheDao;
import com.github.kamismile.stone.commmon.exception.BusinessException;
import com.github.kamismile.stone.commmon.util.JsonUtil;
import com.github.kamismile.stone.commmon.util.ValueUtils;
import io.vertx.core.eventbus.Message;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by lidong on 2017/2/21.
 */

//@Aspect
//@Component
public class ValidTokenService {

//    @Autowired
    UserCacheDao userCacheDao;

//    @Before("@annotation(VertxAddress)")
    public void authToken(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();
        VertxAddress vertxAddress = method.getAnnotation(VertxAddress.class);
        if(args[0] instanceof Message){
            Message msg = (Message) args[0];
            Map<String, Object> requestJson = JsonUtil.json2Map(ValueUtils.isStringNull(msg.body()));
            Assert.notNull(requestJson.get("token"),"token");
           if (ValueUtils.isNull(userCacheDao.getUser(requestJson))){
               throw new BusinessException(400);
           }

        }else {
            throw  new BusinessException(400);
        }

    }


}
