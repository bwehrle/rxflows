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

            final AtomicLong requestTotal = new AtomicLong(0);
            final Scheduler scheduler = Schedulers.newSingle("producer");
            final Scheduler.Worker worker = scheduler.createWorker();
            final AtomicBoolean cancelled = new AtomicBoolean(false);

            worker.schedule(() -> {
                final AtomicBoolean inProgress = new AtomicBoolean(false);
                final AtomicInteger lastValue = new AtomicInteger();
                final int batchSize = 10;

                while (!cancelled.get()) {
                    try {
                        //  With prefetch  ==  1
                        if (requestTotal.get() > 0 && inProgress.compareAndSet(false, true)) {
                            System.out.println("Produced range starting with " + lastValue  + " requested " + requestTotal.get());
                            // Make batchSized observable:
                            //
                            Flux<Integer> rangeObservable =
                                    Flux.range(lastValue.addAndGet(1), batchSize)
                                        .delayElements(Duration.of(10, ChronoUnit.MILLIS))
                                        .doOnNext(x -> {
                                            lastValue.set(x);
                                        })
                                        .doOnComplete(() -> {
                                            System.out.println("Completed, allowing new range to be created");
                                            inProgress.set(false);
                                        });
                            publisherFluxSink.next(rangeObservable);
                            requestTotal.decrementAndGet();
                        } else {
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                        //pass through or not exceptions,  but never InterruptedException
                        System.out.println("Exception: " + e);
                    }
                }
                System.out.println("Stopping: cancelled = " + cancelled.get());
            });

            publisherFluxSink.onCancel(() -> {
                System.out.println("Cancel called!");
                cancelled.set(true);
                scheduler.dispose();
            });

            publisherFluxSink.onRequest(requested -> {
                System.out.println("Requested " + requested);
                requestTotal.addAndGet(requested);

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
