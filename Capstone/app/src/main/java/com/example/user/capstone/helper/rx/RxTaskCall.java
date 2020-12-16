package com.example.user.capstone.helper.rx;


public abstract class RxTaskCall<T> {

    public abstract T doInBackground() throws Exception;

    public abstract void onResult(T result);

    public abstract void onError(Throwable e);
}
