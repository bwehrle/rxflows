package com.roche.modules.rxflow;

import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScheduledBatchedRangeIT {

    @Test
    public void rangeProducer() {
        Scheduler sched = Schedulers.computation();

        BatchedRange<Long, Long> longBatchedRange = new BatchedRange<>(100,
                1,
                BatchGeneratorOfLong::new,
                sched);

        Observable<Long> observable = longBatchedRange.withRange(1L, 5L);
        List<Long> valList = new ArrayList<>();
        observable.toBlocking().forEach(valList::add);
        assertEquals(Arrays.asList(1L, 2L, 3L, 4L, 5L), valList);
    }
}
