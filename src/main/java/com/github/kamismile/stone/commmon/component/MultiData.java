package com.github.kamismile.stone.commmon.component;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiData {
    String beanName() default  "";
    String methodName() default  "save";
}
