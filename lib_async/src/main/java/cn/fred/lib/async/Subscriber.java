package cn.fred.lib.async;

public interface Subscriber<T> {
    void onError(Throwable th);

    void onSuccess(T t);
}

