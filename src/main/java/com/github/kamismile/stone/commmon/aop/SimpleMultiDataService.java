package com.github.kamismile.stone.commmon.aop;


import com.github.kamismile.stone.commmon.component.MultiData;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

//@Aspect
//@Component
public class SimpleMultiDataService {
    @Autowired
    private ApplicationContext context;

    @AfterReturning(pointcut="@annotation(com.github.kamismile.stone.commmon.component.MultiData)",returning = "rvt")
    public void  save(JoinPoint joinPoint,Object rvt) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();
        MultiData multiData = method.getAnnotation(MultiData.class);
        Object bean = context.getBean(multiData.beanName());
        try {
            Method beanMethod = bean.getClass().getMethod(multiData.methodName(),Object.class);
            beanMethod.invoke(bean,rvt);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(rvt);
    }
}
