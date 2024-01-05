package cn.fred.lib.async;

import java.util.concurrent.ExecutorService;

public interface Scheduler {
    void execute(Runnable runnable);
     ExecutorService getExecutor();
}
