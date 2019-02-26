package com.roche.modules.rxsource;


import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static rx.Single.just;

class ConcatSource {


    Observable<Integer> longSource() {
        return Observable.concat(
                Observable.create(new SyncOnSubscribe<Integer, Observable<Integer>>() {

                    int lastValue = 0;
                    AtomicBoolean inProgress = new AtomicBoolean(false);

                    protected Integer generateState() {
                        return lastValue;
                    }


                    protected Integer next(Integer state, Observer<? super Observable<Integer>> observer) {
                        if (inProgress.compareAndSet(false, true)) {
                            System.out.println("Produced range starting with " + lastValue);
                            Observable<Integer> rangeObservable =
                                    Observable.range(lastValue+1, 10)
                                              .delay(100L, TimeUnit.MILLISECONDS)
                                              .doOnNext(x -> lastValue = x)
                                              .doOnCompleted(() -> {
                                                  System.out.println("Completed, allowing new range to be created");
                                                  inProgress.set(false);
                                              });
                            observer.onNext(rangeObservable);
                        }
                        return lastValue;
                    }
                }));

    }
}

public class TestConcatMerging {

    @Test
    public void consume200Events() throws InterruptedException {
        ConcatSource src = new ConcatSource();
        src.longSource()
           .doOnNext( x-> System.out.println("Got " + x))
           .take(20)
           .reduce(
                0,
                (x,y) -> x+y)
           .subscribe( x -> System.out.println("Value " + x));

        Thread.sleep(5000);
    }
}
