package com.roche.modules.rx;

import reactor.core.publisher.Flux;

import java.util.function.Function;


interface FluxCreator<T,S> {

    FluxGenerator<T> createBounded(S initialValue, S endValue);
    FluxGenerator<T> create(S initialValue);
    FluxGenerator<T> createAll(S initialValue);

}

/*
public class LongPublisher implements ReactiveDataSource<Long, Long> {

    private Function<Long, Long> accessFunction;

    public LongPublisher(
            FluxCreator creator,
            Function<Long, Long> accessFunction
    ) {
    }


    public Flux<Long> getPublisher(Long start, Long end) {
        FluxGenerator<Long> publisher;// = generatorFunction.apply(start);
        return Flux.mergeSequential(publisher.getPublisher());
    }

    public Flux<Long> getPublisher(Long start) {
        FluxGenerator<Long> publisher;// = generatorFunction.apply(start);
        return Flux.mergeSequential(publisher.getPublisher());
    }

    public Flux<Long> getPublisher() {
        FluxGenerator<Long> publisher; // = generatorFunction.apply(null);
        return Flux.mergeSequential(publisher.getPublisher());
    }
}
*/