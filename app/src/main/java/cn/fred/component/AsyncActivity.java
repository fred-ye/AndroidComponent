package cn.fred.component;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.concurrent.Callable;

import cn.fred.component.databinding.ActivityAsyncBinding;
import cn.fred.lib.async.Async;
import cn.fred.lib.async.Platform;
import cn.fred.lib.async.Schedulers;
import cn.fred.lib.async.Subscriber;
import cn.fred.lib.async.SubscriberForSuccess;

public class AsyncActivity extends AppCompatActivity {
    ActivityAsyncBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAsyncBinding.inflate(LayoutInflater.from(this));
        initView();
    }

    private void initView() {
       binding.btnAsync.setOnClickListener(v -> Async.create(() -> 1 + 1).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onError(Throwable th) {

            }

            @Override
            public void onSuccess(Integer data) {
                printData(data);
            }
        }));
        binding.btnAsyncMain.setOnClickListener(v -> Async.create(()-> 1 + 1).observableOnMain(new Subscriber<Integer>() {
            @Override
            public void onError(Throwable th) {

            }

            @Override
            public void onSuccess(Integer data) {
                printData(data);
            }
        }));
        binding.btnAsyncSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Async.create(()-> 1 + 1).observableOnMain(new SubscriberForSuccess<Integer>() {
                    @Override
                    public void onSuccess(Integer data) {
                     printData(data);
                    }
                });
            }
        });
        binding.btnThreadPool.setOnClickListener(v -> {
            for (int i = 0; i < 10; i ++) {
                Schedulers.io().execute(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("execute on:" + Thread.currentThread().getName());
                });
            }
        });

    }
    private void printData(Integer data) {
        System.out.println("data: " + data + "   threadName:" + Thread.currentThread().getName());
    }
}