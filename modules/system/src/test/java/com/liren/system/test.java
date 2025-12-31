package com.liren.system;


import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class test {
    static ThreadLocal<String> TL = new ThreadLocal<>();
    static ThreadLocal<String> TTL = new TransmittableThreadLocal<>();

    public static void main1(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        TL.set("trace-123");

        executor.submit(() -> {
            // ❌ 预期：null
            System.out.println("ThreadLocal value = " + TL.get());
        }).get();

        executor.shutdown();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService rawExecutor = Executors.newFixedThreadPool(1);
        ExecutorService executor = TtlExecutors.getTtlExecutorService(rawExecutor);

        TTL.set("trace-123");

        executor.submit(() -> {
            // ✅ 预期：trace-123
            System.out.println("TTL value = " + TTL.get());
        }).get();

        executor.shutdown();
    }
}
