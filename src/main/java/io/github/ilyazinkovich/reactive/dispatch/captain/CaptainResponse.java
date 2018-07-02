package io.github.ilyazinkovich.reactive.dispatch.captain;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.CaptainId;

public class CaptainResponse {

  public final Booking booking;
  public final CaptainId captainId;
  public final boolean accepted;

  CaptainResponse(final Booking booking, final CaptainId captainId, final boolean accepted) {
    this.booking = booking;
    this.captainId = captainId;
    this.accepted = accepted;
  }
}
