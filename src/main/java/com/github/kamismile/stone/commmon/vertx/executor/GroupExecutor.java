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

import org.apache.logging.log4j.LogManager;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupExecutor implements Closeable {
    protected final org.apache.logging.log4j.Logger logger = LogManager.getLogger(GroupExecutor.class);

    protected int groupCount;

    protected int coreThreads;

    protected int maxThreads;

    protected int maxIdleInSecond;

    protected int maxQueueSize;

    // to avoid multiple network thread conflicted when put tasks to executor queue
    private List<ExecutorService> executorList = new ArrayList<>();

    // for bind network thread to one executor
    // it's impossible that has too many network thread, so index will not too big that less than 0
    private AtomicInteger index = new AtomicInteger();

    private Map<String, Executor> threadExecutorMap = new ConcurrentHashMapEx<>();


    public GroupExecutor(int groupCount, int coreThreads, int maxThreads, int maxIdleInSecond, int maxQueueSize) {
        this.groupCount = groupCount;
        this.coreThreads = coreThreads;
        this.maxThreads = maxThreads;
        this.maxIdleInSecond = maxIdleInSecond;
        this.maxQueueSize = maxQueueSize;
        logger.info(
                "executor group={}. per group settings, coreThreads={}, maxThreads={}, maxIdleInSecond={}, maxQueueSize={}.",
                groupCount, coreThreads, maxThreads, maxIdleInSecond, maxQueueSize);
        init();
    }

    public void init() {
        for (int groupIdx = 0; groupIdx < groupCount; groupIdx++) {
            ThreadPoolExecutorEx executor = new ThreadPoolExecutorEx(coreThreads,
                    maxThreads,
                    maxIdleInSecond,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueueEx<>(maxQueueSize));
            executorList.add(executor);
        }
    }


    public List<ExecutorService> getExecutorList() {
        return executorList;
    }


    public Executor chooseExecutor(String address) {
//        int idx = index.getAndIncrement() % executorList.size();
        return threadExecutorMap.computeIfAbsent(address, this::getExecutor);
    }

    private Executor getExecutor(String address) {
        int size = executorList.size() - 1;
        if (size < 1) {
            size = 1;
        }
        int idx = Math.abs(address.hashCode()) % size;
        if (address.equals("consumer@version@syn")) {
            return executorList.get(size);
        }
//        if (idx == 0) {
//            idx = RandomUtils.nextInt(1, executorList.size());
//        }
        return executorList.get(idx);
    }


    @Override
    public void close() {
        for (ExecutorService executorService : executorList) {
            executorService.shutdown();
        }
        executorList.clear();
    }

}
