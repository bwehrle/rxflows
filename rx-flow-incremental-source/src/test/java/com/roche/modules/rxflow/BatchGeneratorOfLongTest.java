package com.roche.modules.rxflow;

import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;

import static org.junit.Assert.*;

public class BatchGeneratorOfLongTest {

    @Test
    public void testShortSeries() {
        BatchGeneratorOfLong bpl = new BatchGeneratorOfLong(1L, 5L, 2L);
        assertTrue(bpl.hasNext());
        assertEquals(Arrays.asList(1L, 2L), bpl.getNext() );
        assertTrue(bpl.hasNext());
        assertEquals(Arrays.asList(3L, 4L), bpl.getNext() );
        assertTrue(bpl.hasNext());
        assertEquals(Arrays.asList(5L), bpl.getNext() );
        assertTrue(!bpl.hasNext());
    }

}