package io.github.kamismile.stone.commmon.vertx.invocation;

import java.util.concurrent.CompletableFuture;

public class InvocationContextCompletableFuture<T> extends CompletableFuture<T> {
    private InvocationContext context;

    public InvocationContextCompletableFuture(InvocationContext context) {
        this.context = context;
    }

    public InvocationContext getContext() {
        return context;
    }
}
