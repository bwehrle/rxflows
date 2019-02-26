package com.roche.modules.rx;

import reactor.core.publisher.Flux;

interface FluxGenerator <T> {

    Flux<Flux<T>> getPublisher();
}

public class FluxLongGenerator implements FluxGenerator<Long> {

    private int next;
    private int last;
    private final static int batchSize = 5;
    private final Flux<Flux<Long>> publisher;

    FluxLongGenerator(int initialValue, int lastValue) {
        next = initialValue;
        last = lastValue;
        publisher = Flux.create( emitter ->
                emitter.onRequest(requested -> {
                    if (next < last) {
                        emitter.next(createSource());
                    } else {
                        emitter.complete();
                    }
                })
        );
    }

    private Flux<Long> createSource() {
        int first = next;
        next = next + batchSize;
        return Flux.range(first,  batchSize)
                .map(Long::new)
                .takeUntil( l -> l > last);
    }

    public Flux<Flux<Long>> getPublisher() {
        return publisher;
    }
}
