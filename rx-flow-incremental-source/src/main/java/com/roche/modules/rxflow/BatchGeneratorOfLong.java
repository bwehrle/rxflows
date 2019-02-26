package com.roche.modules.rxflow;


import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

class BatchGeneratorOfLong implements BatchGenerator<Long> {

    protected final Long to, batchSize;
    protected Long current;

    BatchGeneratorOfLong(Long from, Long to, Long batchSize) {
        this.to = to;
        this.batchSize = batchSize;
        this.current = from;
    }

    public boolean hasNext() {
        return (to == null) || current < to + 1;
    }

    public Collection<Long> getNext() {
        long startRange = current;
        long endRange = getEndRange(startRange);
        current = endRange;
        return LongStream
                .range(startRange, endRange)
                .boxed()
                .collect(Collectors.toList());
    }

    protected long getEndRange(Long startRange) {
        if (to == null) {
            return (startRange + batchSize);
        }
        return ((startRange + batchSize) > to + 1) ? (to + 1): (startRange + batchSize);
    }
}

