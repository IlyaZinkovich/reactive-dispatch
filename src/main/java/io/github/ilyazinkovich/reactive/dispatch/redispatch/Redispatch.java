package io.github.ilyazinkovich.reactive.dispatch.redispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.BookingId;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

public class Redispatch {

  private final Map<BookingId, AtomicInteger> retriesCount;
  private final Duration retryDelay;
  private final Scheduler retryScheduler;
  private final int maxRetriesCount;
  private final FailedDispatchBookings failedDispatchBookings;

  public Redispatch(final int maxRetriesCount, final Duration retryDelay,
      final Scheduler retryScheduler, final Map<BookingId, AtomicInteger> retriesCount,
      final FailedDispatchBookings failedDispatchBookings) {
    this.maxRetriesCount = maxRetriesCount;
    this.retriesCount = retriesCount;
    this.retryDelay = retryDelay;
    this.retryScheduler = retryScheduler;
    this.failedDispatchBookings = failedDispatchBookings;
  }

  public Mono<Booking> scheduleReDispatch(final Booking booking) {
    retriesCount.putIfAbsent(booking.id, new AtomicInteger());
    if (retriesCount.get(booking.id).incrementAndGet() > maxRetriesCount) {
      failedDispatchBookings.add(booking);
      return Mono.empty();
    } else {
      return Mono.just(booking).delayElement(retryDelay, retryScheduler);
    }
  }
}
