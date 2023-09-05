package com.mineaurion.aurioneconomy.common.plugin.scheduler;

import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyBootstrap;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public abstract class AbstractJavaScheduler implements SchedulerAdapter {

    private static final int PARALLELISM = 16;
    private final AurionEconomyBootstrap bootstrap;
    private final ScheduledThreadPoolExecutor scheduler;
    private final ForkJoinPool worker;

    public AbstractJavaScheduler(AurionEconomyBootstrap bootstrap){
        this.bootstrap = bootstrap;

        this.scheduler = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName(AurionEconomyPlugin.MOD_ID + "-scheduler");
            return thread;
        });
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.worker = new ForkJoinPool(PARALLELISM, new WorkerThreadFactory(), new ExceptionHandler(), false);
    }

    @Override
    public Executor async() {
        return this.worker;
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.schedule(() -> this.worker.execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.worker.execute(task), interval, interval, unit);
        return  () -> future.cancel(false);
    }

    @Override
    public void shutdownScheduler() {
        this.scheduler.shutdown();
        try {
            if(!this.scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                //TODO: better logging
                System.out.println("Timed out waiting for " + AurionEconomyPlugin.NAME + " scheduler to terminate");
                reportRunningTasks(thread -> thread.getName().equals(AurionEconomyPlugin.MOD_ID + "-scheduler"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownExecutor() {
        this.worker.shutdown();
        try {
            if(!this.worker.awaitTermination(1, TimeUnit.MINUTES)){
                System.out.println("Timed out waiting for " + AurionEconomyPlugin.NAME + " worker thread pool to terminate");
                reportRunningTasks(thread -> thread.getName().startsWith(AurionEconomyPlugin.MOD_ID + "-worker-"));
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void reportRunningTasks(Predicate<Thread> predicate){
        Thread.getAllStackTraces().forEach(((thread, stack) -> {
            if(predicate.test(thread)){
                System.out.println("Thread " + thread.getName() + " is blocked and this is why the shutdown is slow\n" + Arrays.stream(stack).map(el -> " " + el).collect(Collectors.joining("\n")));
            }
        }));
    }

    private static final class WorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private static final AtomicInteger COUNT = new AtomicInteger(0);

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setDaemon(true);
            thread.setName(AurionEconomyPlugin.MOD_ID + "-worker-" + COUNT.getAndIncrement());
            return thread;
        }
    }

    private static final class ExceptionHandler implements UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("Thread " + t.getName() + " threw an uncaught exception");
            System.out.println(e.getMessage());
        }
    }
}
