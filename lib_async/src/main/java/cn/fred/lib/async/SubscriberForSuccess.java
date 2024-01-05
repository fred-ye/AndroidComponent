package cn.fred.lib.async;

public abstract class SubscriberForSuccess<T> implements Subscriber<T> {

    @Override
    public void onError(Throwable th) {
        th.printStackTrace();
    }
}
