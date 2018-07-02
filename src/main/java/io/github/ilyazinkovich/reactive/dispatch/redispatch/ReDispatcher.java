package io.github.ilyazinkovich.reactive.dispatch.redispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.offer.ReDispatch;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class ReDispatcher {

  public ReDispatcher(final PublishSubject<Booking> bookingsSubject,
      final Flowable<ReDispatch> reDispatchesStream) {
    reDispatchesStream.subscribe(reDispatch -> bookingsSubject.onNext(reDispatch.booking));
  }
}
