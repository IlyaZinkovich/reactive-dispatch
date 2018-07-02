package io.github.ilyazinkovich.reactive.dispatch.offer;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.BookingId;
import io.github.ilyazinkovich.reactive.dispatch.core.CaptainId;

public class Offer {

  public final Booking booking;
  public final CaptainId captainId;

  public Offer(final Booking booking,
      final CaptainId captainId) {
    this.booking = booking;
    this.captainId = captainId;
  }
}
