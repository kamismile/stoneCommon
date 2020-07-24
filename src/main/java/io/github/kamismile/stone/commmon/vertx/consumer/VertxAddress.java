package io.github.kamismile.stone.commmon.vertx.consumer;

import java.lang.annotation.*;

/**
 * Created by lidong on 2017/2/20.
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VertxAddress {

    String value() default  "";

    /**
     * 1的情况追加标识
     * @return
     */
    int isRouter() default 0;


    /**
     * 是否要前缀 0 不加
     * @return
     */
    int isPrefix() default 1;
}
