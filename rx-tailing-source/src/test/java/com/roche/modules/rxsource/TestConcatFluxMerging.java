package com.roche.modules.rxsource;


import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import rx.Observable;
import rx.Observer;
import rx.internal.operators.BackpressureUtils;
import rx.observables.SyncOnSubscribe;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class ConcatSourceFlux {


    Flux<Integer> longSource() {
        Flux<Flux<Integer>> fluxCreator = Flux.create(publisherFluxSink -> {

            final AtomicInteger lastValue = new AtomicInteger();
            final int batchSize = 10;

            publisherFluxSink.onCancel(() -> {
                System.out.println("Cancel called!");
            });

            publisherFluxSink.onRequest(requested -> {
                System.out.println("Requested " + requested);
                Flux<Integer> rangeObservable =
                        Flux.range(lastValue.addAndGet(1), batchSize)
                            .delayElements(Duration.of(10, ChronoUnit.MILLIS))
                            .doOnNext(x -> {
                                lastValue.set(x);
                            })
                            .doOnComplete(() -> {
                                System.out.println("Completed, allowing new range to be created");
                            });
                publisherFluxSink.next(rangeObservable);
            });
        });
        return Flux.concat(fluxCreator, 1);

    }
}


public class TestConcatFluxMerging {

    @Test
    public void consume200Events() throws InterruptedException {
        ConcatSourceFlux src = new ConcatSourceFlux();
        src.longSource()
           .doOnNext(x -> System.out.println("Got " + x))
           .take(50)
           .reduce(
                   0,
                   (x, y) -> x + y)
           .subscribe(x -> System.out.println("Value " + x));

        Thread.sleep(5000);
    }
}
