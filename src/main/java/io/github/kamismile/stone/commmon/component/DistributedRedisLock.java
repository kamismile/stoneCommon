package io.github.kamismile.stone.commmon.component;


import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedRedisLock {

    String obtain() default  "";
    int timeOut() default 1;
    int code() default 1;
    String message() default "";
}
