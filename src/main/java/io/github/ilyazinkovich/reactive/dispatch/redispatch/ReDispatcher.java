package io.github.ilyazinkovich.reactive.dispatch.redispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.BookingId;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

public class ReDispatcher {

  private final Map<BookingId, AtomicInteger> retriesCount;
  private final Duration retryDelay;
  private final Scheduler retryScheduler;
  private final int maxRetriesCount;
  private final FailedDispatchBookingsConsumer failedDispatchBookingsConsumer;

  public ReDispatcher(final int maxRetriesCount, final Duration retryDelay,
      final Scheduler retryScheduler, final Map<BookingId, AtomicInteger> retriesCount,
      final FailedDispatchBookingsConsumer failedDispatchBookingsConsumer) {
    this.maxRetriesCount = maxRetriesCount;
    this.retriesCount = retriesCount;
    this.retryDelay = retryDelay;
    this.retryScheduler = retryScheduler;
    this.failedDispatchBookingsConsumer = failedDispatchBookingsConsumer;
  }

  public Mono<Booking> accept(final Booking booking) {
    retriesCount.putIfAbsent(booking.id, new AtomicInteger());
    if (retriesCount.get(booking.id).incrementAndGet() > maxRetriesCount) {
      failedDispatchBookingsConsumer.accept(booking);
      return Mono.empty();
    } else {
      return Mono.just(booking).delayElement(retryDelay, retryScheduler);
    }
  }
}
