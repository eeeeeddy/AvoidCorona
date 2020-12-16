package com.example.user.capstone.helper.rx;


import com.example.user.capstone.helper.L;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RxHelper {
    public static Disposable delay(long delayTime, @NonNull final RxCall<Long> call) {
        return Flowable.timer(delayTime, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        call.onCall(aLong);
                    }
                }, e -> e.printStackTrace(), () -> {
                    L.e("onComplete............");
                });
    }

    public static <T> Disposable runOnBackground(@NonNull final RxTaskCall<T> call) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                T result = call.doInBackground();
                if (result == null) {
                    emitter.onError(new NullPointerException());
                    emitter.onComplete();
                } else {
                    emitter.onNext(result);
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(call::onResult, call::onError);

    }


}
