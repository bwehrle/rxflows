package com.roche.modules.rxflow;

import rx.Observable;
import rx.Scheduler;

import static rx.Observable.unsafeCreate;

public class BatchedRange<T, S> implements Range<T, S> {

    private final long delayMs;
    private final long batchSize;
    private final Scheduler scheduler;
    private final BatchGeneratorFactory<T,S> factory;

    public BatchedRange(long delayMs, long batchSize, BatchGeneratorFactory<T,S> factory, Scheduler scheduler) {
        this.delayMs = delayMs;
        this.batchSize = batchSize;
        this.factory = factory;
        this.scheduler = scheduler;

    }

    public Observable<T> withRange(S from, S to) {
        return unsafeCreate(
                new ScheduledIntervalProducer<>(
                    factory.create(from, to, batchSize),
                    delayMs,
                    scheduler)
        );
    }

    public Observable<T> withRange(S from) {
        return unsafeCreate(
                new ScheduledIntervalProducer<>(
                        factory.create(from, null, batchSize),
                        delayMs,
                        scheduler)
        );
    }

}
