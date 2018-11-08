package io.github.ilyazinkovich.reactive.dispatch.core;

public class RedispatchRequired extends RuntimeException {

  public final transient Booking booking;

  public RedispatchRequired(final Booking booking) {
    this.booking = booking;
  }
}
