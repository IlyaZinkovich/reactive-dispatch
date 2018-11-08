package io.github.ilyazinkovich.reactive.dispatch.core;

public class RedispatchRequiredException extends RuntimeException {

  public final Booking booking;

  public RedispatchRequiredException(final Booking booking) {
    this.booking = booking;
  }
}
