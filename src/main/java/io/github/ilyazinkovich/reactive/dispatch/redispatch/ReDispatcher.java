package io.github.ilyazinkovich.reactive.dispatch.redispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.BookingId;
import io.github.ilyazinkovich.reactive.dispatch.offer.ReDispatch;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ReDispatcher implements Consumer<ReDispatch> {

  private final PublishSubject<Booking> bookingsSubject;
  private final Map<BookingId, AtomicInteger> retriesCount;

  public ReDispatcher(final PublishSubject<Booking> bookingsSubject,
      final Map<BookingId, AtomicInteger> retriesCount) {
    this.bookingsSubject = bookingsSubject;
    this.retriesCount = retriesCount;
  }

  @Override
  public void accept(final ReDispatch reDispatch) {
    final BookingId bookingId = reDispatch.booking.id;
    retriesCount.putIfAbsent(bookingId, new AtomicInteger());
    if (retriesCount.get(bookingId).incrementAndGet() > 3) {
      System.out.printf("Retries count exceeded for booking %s%n", bookingId.uid);
    } else {
      bookingsSubject.onNext(reDispatch.booking);
    }
  }
}
