package com.roche.modules.rxflow;

import rx.Observable;
import rx.Producer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


// Cold producer with out backpressure support
//
class ScheduledIntervalProducer<T> implements Observable.OnSubscribe<T> {

    private final BatchGenerator<T> batchGenerator;
    private final Scheduler scheduler;
    private final Queue<T> backlog;

    ScheduledIntervalProducer(BatchGenerator<T> batchGenerator, Long delayMs, Scheduler scheduler) {
        this.batchGenerator = batchGenerator;
        this.scheduler = scheduler;
        this.backlog = new LinkedList<>();
    }

    public void call(final Subscriber<? super T> subscriber) {
        Scheduler.Worker worker = scheduler.createWorker();
        subscriber.add(worker);
        subscriber.setProducer(new Producer() {
            @Override
            public void request(long n) {

                if (!subscriber.isUnsubscribed()) {
                    while (n>0 && !backlog.isEmpty()) {
                        subscriber.onNext(backlog.remove());
                        n--;
                    }
                }

                if (!batchGenerator.hasNext()) {
                    subscriber.onCompleted();
                    worker.unsubscribe();
                }
                else {
                    worker.schedule( () -> {
                        if (!subscriber.isUnsubscribed()) {
                            Collection<T> batch = batchGenerator.getNext();
                            backlog.addAll(batch);
                        }
                    });

                }
            }
        });



    }
}