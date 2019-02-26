package com.roche.modules.rxflow;

import java.util.Collection;

// This should output immutables
public interface BatchGenerator<T> {

    boolean hasNext();
    Collection<T> getNext();
}
