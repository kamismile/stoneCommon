package io.github.kamismile.stone.commmon.vertx.provider;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VertxReference {

    /**
     * 毫秒
     * @return
     */
    int timeout() default  30000;
}
