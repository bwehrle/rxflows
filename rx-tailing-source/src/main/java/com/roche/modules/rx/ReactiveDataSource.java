package com.roche.modules.rx;

import reactor.core.publisher.Flux;

public interface ReactiveDataSource<T, S> {

    Flux<T> getPublisher(S startRange, S endRange);

    Flux<T> getPublisher(S startRange);

    Flux<T> getPublisher();
}
