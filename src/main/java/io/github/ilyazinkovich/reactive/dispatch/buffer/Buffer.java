package io.github.ilyazinkovich.reactive.dispatch.buffer;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import java.time.Duration;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

public class Buffer {

  private final Scheduler batchingScheduler;

  public Buffer(final Scheduler batchingScheduler) {
    this.batchingScheduler = batchingScheduler;
  }

  public Flux<List<Booking>> formBucket(final Flux<Booking> bookings) {
    return bookings.bufferTimeout(10, Duration.ofSeconds(2), batchingScheduler);
  }
}
