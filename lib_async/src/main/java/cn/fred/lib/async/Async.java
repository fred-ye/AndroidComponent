package cn.fred.lib.async;

import java.util.concurrent.Callable;

public class Async<T> {
    private final Callable<T> callable;
    private Scheduler subscribeOnExecutor;
    private Scheduler observeOnExecutor;
    private Async(Callable<T> callable) {
        this.callable = callable;
    }

    public static<T> Async<T> create(Callable<T> callable) {
        return new Async<>(callable);
    }

    /**
     * 执行线程
     */
    public Async<T> subscribeOn(Scheduler executor) {
        this.subscribeOnExecutor = executor;
        return this;
    }

    /**
     * 结果返回的线程
     */
    public Async<T> observeOn(Scheduler executor) {
        this.observeOnExecutor = executor;
        return this;
    }

    /**
     * 异步框架大部份的使用场景都是在子线程执行耗时任务，将任务结果返回给主线程
     */
    public void observableOnMain(Subscriber<T> subscriber) {
        subscribeOnExecutor = Schedulers.io();
        observeOnExecutor = Schedulers.main();
        subscribe(subscriber);
    }
    public void subscribe(Subscriber<T> subscriber) {
        if (subscribeOnExecutor != null) {
            subscribeOnExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        T result = callable.call();
                        if (observeOnExecutor != null) {
                            observeOnExecutor.execute(() -> subscriber.onSuccess(result));
                        } else {
                            subscriber.onSuccess(result);
                        }
                    } catch (Exception e) {
                        if (observeOnExecutor != null) {
                            observeOnExecutor.execute(() -> subscriber.onError(e));
                        } else {
                            subscriber.onError(e);
                        }
                    }
                }
            });
        }
    }
}
