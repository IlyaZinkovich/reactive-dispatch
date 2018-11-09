package io.github.ilyazinkovich.reactive.dispatch.buffer;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

public class Buffer {

  private final Scheduler batchingScheduler;

  public Buffer(final Scheduler batchingScheduler) {
    this.batchingScheduler = batchingScheduler;
  }

  public Flux<Flux<Booking>> formBucket(Flux<Booking> bookings) {
    return bookings.windowTimeout(10, Duration.ofSeconds(2), batchingScheduler);
  }
}
