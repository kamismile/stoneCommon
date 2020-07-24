package io.github.kamismile.stone.commmon.vertx.invocation;

import io.github.kamismile.stone.commmon.base.AppResponse;
import io.github.kamismile.stone.commmon.vertx.starter.AsyncResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class InvokerUtils {
    protected static final Logger logger = LogManager.getLogger(InvokerUtils.class);

    /**
     * it's a internal API, caller make sure already invoked SCBEngine.ensureStatusUp
     *
     * @param invocation
     * @param asyncResp
     */
    public static void reactiveInvoke(Invocation invocation, AsyncResponse asyncResp) {
        AppResponse response = new AppResponse();

        try {
            invocation.setSync(false);

            invocation.next(ar -> {
                response.setSuccess(ar.isSuccess());
                response.setResultVO(ar.getResultVO());
                asyncResp.handle(response);
            });
        } catch (Throwable e) {
            //if throw exception,we can use 500 for status code ?
            logger.error("invoke failed, {}", ExceptionUtils.getFullStackTrace(e));
            response.setSuccess(false);
            asyncResp.handle(response);
        }
    }

    public static AppResponse innerSyncInvoke(Invocation invocation) {
        AppResponse appResponse = new AppResponse();
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            invocation.next(ar -> {
                appResponse.setResultVO(ar.getResultVO());
                appResponse.setSuccess(ar.isSuccess());
                countDownLatch.countDown();
            });
            if (invocation.getTimeout() > 0) {
                countDownLatch.await(invocation.getTimeout(), TimeUnit.MILLISECONDS);
            } else {
                countDownLatch.await();
            }
        } catch (Throwable e) {
            appResponse.setSuccess(false);
            String msg = String.format("invoke failed, %s", ExceptionUtils.getFullStackTrace(e));
            logger.error(msg);
        }

        return appResponse;
    }
}
