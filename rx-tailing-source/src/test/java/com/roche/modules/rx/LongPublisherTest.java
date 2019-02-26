package com.roche.modules.rx;

import org.junit.Assert;
import org.junit.Test;

public class LongPublisherTest {

    @Test
    public void subscripter_readsCorrectList() {
        ReactiveDataSource<Long,Long> cs = null;
        Long count = cs.getPublisher(1L, 20L)
                .count()
                .block();
        Assert.assertEquals((Long)20L, count);
    }
}
