package com.github.kamismile.stone.commmon.dao;

import com.github.kamismile.stone.commmon.exception.BusinessException;
import com.github.kamismile.stone.commmon.util.JsonUtil;
import com.github.kamismile.stone.commmon.util.ValueUtils;
import org.springframework.cache.Cache;

import java.util.Map;

/**
 * Created by lidong on 2017/2/24.
 */
//@Repository
public class UserCacheDao extends CommonCacheDao {


    public Map<String,Object> getUser(Map<String,Object> json){
        Cache.ValueWrapper uid = getUserByToken(json);
        if(ValueUtils.isNull(uid)){
            throw new BusinessException(400);
        }
        return JsonUtil.json2Map(ValueUtils.isStringNull(uid.get()));
    }


    public Cache.ValueWrapper getUserByToken(Map<String,Object> json){
        Cache.ValueWrapper user = getCache("cacheShort","authT:"+json.get("token"));
        return user;
    }

}
