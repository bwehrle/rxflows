package com.roche.modules.rxflow;


import rx.Observable;


public interface Range<T,D> {
    Observable<T> withRange(D from, D to);
    Observable<T> withRange(D from);
}
