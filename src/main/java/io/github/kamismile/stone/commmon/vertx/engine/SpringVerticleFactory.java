package io.github.kamismile.stone.commmon.vertx.engine;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringVerticleFactory implements VerticleFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public boolean blockingCreate() {
        // Usually verticle instantiation is fast but since our verticles are Spring Beans,
        // they might depend on other beans/resources which are slow to build/lookup.
        return true;
    }

    @Override
    public String prefix() {
        // Just an arbitrary string which must uniquely identify the verticle factory
        return "stone";
    }

    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        // Our convention in this example is to give the class name as verticle name
        String clazz = VerticleFactory.removePrefix(verticleName);
        return (Verticle) applicationContext.getBean(Class.forName(clazz));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
