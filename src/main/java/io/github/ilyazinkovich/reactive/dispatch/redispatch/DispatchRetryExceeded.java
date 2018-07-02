package io.github.ilyazinkovich.reactive.dispatch.redispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;

public class DispatchRetryExceeded {

  private final Booking booking;

  public DispatchRetryExceeded(final Booking booking) {
    this.booking = booking;
  }
}
