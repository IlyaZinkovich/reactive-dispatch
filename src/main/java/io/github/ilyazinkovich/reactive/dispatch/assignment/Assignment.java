package io.github.ilyazinkovich.reactive.dispatch.assignment;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.CaptainId;

public class Assignment {

  public final Booking booking;
  public final CaptainId captainId;

  public Assignment(final Booking booking, final CaptainId captainId) {
    this.booking = booking;
    this.captainId = captainId;
  }
}
