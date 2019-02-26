package com.roche.modules.rxflow;

public interface BatchGeneratorFactory<T,S> {
    BatchGenerator<T> create(S from, S to, Long batchSize);
}
