/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, ConsumerVersion 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kamismile.stone.commmon.vertx.executor;


//import co.paralleluniverse.fibers.FiberForkJoinScheduler;
import com.github.kamismile.stone.commmon.util.ValueUtils;
import com.github.kamismile.stone.commmon.vertx.ServiceVertxConfigProperties;

import java.util.concurrent.Executor;

public final class ExecutorManager {

    private static GroupExecutor groupExecutor;
//    private static FiberForkJoinScheduler FIBER_FORK_JOIN_SCHEDULER = new FiberForkJoinScheduler("fiber", Runtime.getRuntime().availableProcessors() * 2);

    private ExecutorManager() {
        Integer groupCount = ValueUtils.isIntegerNull(ServiceVertxConfigProperties.getGroupCount(), 3);
        Integer coreThreads = ValueUtils.isIntegerNull(ServiceVertxConfigProperties.getCoreThreads(), Runtime.getRuntime().availableProcessors() * 2);
        Integer maxThreads = ValueUtils.isIntegerNull(ServiceVertxConfigProperties.getMaxThreads(), Runtime.getRuntime().availableProcessors() * 10);
        Integer maxIdleInSecond = ValueUtils.isIntegerNull(ServiceVertxConfigProperties.getMaxIdleInSecond(), 60);
        Integer maxQueueSize = ValueUtils.isIntegerNull(ServiceVertxConfigProperties.getMaxQueueSize(), 2000);

        groupExecutor = new GroupExecutor(groupCount, coreThreads, maxThreads, maxIdleInSecond, maxQueueSize);

    }

    public static Executor findExecutor(String address) {
        return ExecutorManagerHolder.INSTANCE.groupExecutor.chooseExecutor(address);
    }

    private static class ExecutorManagerHolder {
        private final static ExecutorManager INSTANCE = new ExecutorManager();
    }

//    public static FiberForkJoinScheduler getFiberForkJoinScheduler() {
//        return FIBER_FORK_JOIN_SCHEDULER;
//    }
}
