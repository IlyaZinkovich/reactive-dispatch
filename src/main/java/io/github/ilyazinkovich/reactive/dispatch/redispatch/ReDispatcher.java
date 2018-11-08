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

  public ReDispatcher(final int maxRetriesCount, final Duration retryDelay,
      final Scheduler retryScheduler, final Map<BookingId, AtomicInteger> retriesCount) {
    this.maxRetriesCount = maxRetriesCount;
    this.retriesCount = retriesCount;
    this.retryDelay = retryDelay;
    this.retryScheduler = retryScheduler;
  }

  public Mono<Booking> accept(final Booking booking) {
    retriesCount.putIfAbsent(booking.id, new AtomicInteger());
    if (retriesCount.get(booking.id).incrementAndGet() > maxRetriesCount) {
      return Mono.empty();
    } else {
      return Mono.just(booking).delayElement(retryDelay, retryScheduler);
    }
  }
}
