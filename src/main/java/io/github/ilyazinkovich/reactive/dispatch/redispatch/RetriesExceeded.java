package io.github.ilyazinkovich.reactive.dispatch.redispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;

public class RetriesExceeded {

  private final Booking booking;

  public RetriesExceeded(final Booking booking) {
    this.booking = booking;
  }
}
