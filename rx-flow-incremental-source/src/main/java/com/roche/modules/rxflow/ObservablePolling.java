package com.roche.modules.rxflow;

import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.internal.operators.BackpressureUtils;

import java.util.concurrent.atomic.AtomicLong;

class LongStream {

    Observable<Long> getNext() {
        return Observable.range(10, 100).map(Long::new);
    }
}

public class ObservablePolling implements Observable.OnSubscribe<Long> {

    private Subscriber<Long> child;

    public ObservablePolling() {
    }

    @Override
    public void call(Subscriber<? super Long> subscriber) {
        if (!subscriber.isUnsubscribed()) {
            subscriber.setProducer(new InternalProducer(subscriber));
        }
    }

    class InternalProducer implements Producer {

        private final Subscriber<? super Long> subscriber;
        private AtomicLong requestCounter;

        public InternalProducer(Subscriber<? super Long> subscriber) {
            this.subscriber = subscriber;
            this.requestCounter = new AtomicLong();
        }

        @Override
        public void request(long n) {
            if (BackpressureUtils.getAndAddRequest(requestCounter, n) == 0) {
                // request more data

            }
        }
    }
}
