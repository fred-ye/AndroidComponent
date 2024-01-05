package cn.fred.lib.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Schedulers {
    static final int CPU = Runtime.getRuntime().availableProcessors();
    public static final class IOScheduler implements Scheduler {
        /**
         * 核心线程数：通常可以将核心线程数设置为0, IO线程池不需要响应的及时性，所以将常驻线程设置为0，可以减少应用的线程数量
         * 最大线程数：通常中小型，业务比较简单设置成64即可。
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 64, 1,
                TimeUnit.MINUTES, new SynchronousQueue<>(), new MobileThreadFactory("IOScheduler"));
        @Override
        public void execute(Runnable runnable) {
            executor.execute(runnable);
        }
        public ExecutorService getExecutor() {
            return executor;
        }
    }

    private static final class ComputationScheduler implements Scheduler {
        /**
         * 核心线程：将核心线程数设置为该手机的 CPU 核数，理想状态下每一个核可以运行一个线程，这样能减少 CPU 线程池的调度损耗又能充分发挥 CPU 性能。
         * 最大线程数：和核心线程保持一致，因为当最大线程数超过了核心线程数时，反倒会降低 CPU 的利用率，因为此时会把更多的 CPU 资源用于线程调度上，
         */
        ExecutorService executor = new ThreadPoolExecutor(Schedulers.CPU, Schedulers.CPU, 1,
                TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>(),  new MobileThreadFactory("ComputationScheduler"));

        @Override
        public void execute(Runnable runnable) {
            executor.execute(runnable);
        }

        @Override
        public ExecutorService getExecutor() {
            return executor;
        }
    }
    private static class IOSchedulerHolder {
        private static final IOScheduler INSTANCE = new IOScheduler();
    }
    public static Scheduler io() {
        return IOSchedulerHolder.INSTANCE;
    }
    private static class ComputationSchedulerHolder {
        private static final ComputationScheduler INSTANCE = new ComputationScheduler();
    }
    public static Scheduler computation() {
        return ComputationSchedulerHolder.INSTANCE;
    }

    private static class MainSchedulerHolder {
        private static final Main INSTANCE = new Main();
    }
    public static Scheduler main() { return MainSchedulerHolder.INSTANCE;}

    private static class Main implements Scheduler {
        @Override
        public void execute(Runnable runnable) {
             Platform.get().execute(runnable);
        }

        @Override
        public ExecutorService getExecutor() {
            return null;
        }
    }
}
