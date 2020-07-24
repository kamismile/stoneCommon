/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.github.kamismile.stone.commmon.vertx.exception;

import com.google.gson.JsonSyntaxException;
import io.github.kamismile.stone.commmon.base.ResultVO;
import io.vertx.core.eventbus.Message;
import io.github.kamismile.stone.commmon.exception.BusinessException;
import io.github.kamismile.stone.commmon.vertx.definition.SimpleJsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * @author dong.li
 * @version $Id: DefaultErrorVertxExceptionHandler, v 0.1 2020/3/26 16:34 dong.li Exp $
 */
public class DefaultErrorVertxExceptionHandler implements VertxExException {

    protected final Logger logger = LogManager.getLogger("bLog");

    @Override
    public void handle(String address, Message message, Throwable e) {
        logger.error("ex-->{},address-->{}", address, ExceptionUtils.getStackTrace(e));
        ResultVO resultVO = new ResultVO();
        resultVO.setCode("-1");
        resultVO.setMessage("error");
        if (e instanceof InvocationTargetException) {
            Throwable t = ((InvocationTargetException) e).getTargetException();
            if (t instanceof IllegalArgumentException) {
                resultVO.setCode("2");
                resultVO.setMessage(t.getMessage());
            } else if (t instanceof JsonSyntaxException) {
                resultVO.setCode("2");
                resultVO.setMessage("{\"message\":\"json Exception\"}");
            } else if (t instanceof BusinessException) {
                BusinessException bx = (BusinessException) t;
                resultVO.setCode(bx.getCode() + "");
                resultVO.setMessage(bx.getMessage());
                resultVO.setData(bx.getData());
            }
        }
        SimpleJsonObject jsonObject = new SimpleJsonObject();

        jsonObject.put("resultVO", resultVO);
        try {
            message.reply(jsonObject);
        } catch (Exception ex) {
            logger.error("ex-->{},address-->{}", address, ExceptionUtils.getStackTrace(ex));
        }
    }
}


