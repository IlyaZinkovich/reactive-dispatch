package io.github.ilyazinkovich.reactive.dispatch.redispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.BookingId;
import io.github.ilyazinkovich.reactive.dispatch.core.ReDispatch;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ReDispatcher {

  private final Consumer<Booking> bookingsSubject;
  private final Map<BookingId, AtomicInteger> retriesCount;
  private final Consumer<DispatchRetryExceeded> dispatchRetryExceededSubject;

  public ReDispatcher(final Consumer<Booking> bookingsSubject,
      final Map<BookingId, AtomicInteger> retriesCount,
      final Consumer<DispatchRetryExceeded> dispatchRetryExceededSubject) {
    this.bookingsSubject = bookingsSubject;
    this.retriesCount = retriesCount;
    this.dispatchRetryExceededSubject = dispatchRetryExceededSubject;
  }

  public void accept(final ReDispatch reDispatch) {
    final BookingId bookingId = reDispatch.booking.id;
    retriesCount.putIfAbsent(bookingId, new AtomicInteger());
    if (retriesCount.get(bookingId).incrementAndGet() < 3) {
      bookingsSubject.accept(reDispatch.booking);
    } else {
      System.out.printf("Retries count exceeded for booking %s%n", bookingId.uid);
      dispatchRetryExceededSubject.accept(new DispatchRetryExceeded(reDispatch.booking));
    }
  }
}
