package com.github.kamismile.stone.commmon.vertx.definition;


import io.vertx.core.json.JsonObject;

public class SimpleJsonObject extends JsonObject {

    @Override
    public JsonObject put(String key, Object value) {
        getMap().put(key, value);
        return this;
    }

    @Override
    public JsonObject copy() {
        return this;
    }
}
