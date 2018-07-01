package io.github.ilyazinkovich.reactive.dispatch;

public class Offer {

  final BookingId bookingId;
  final CaptainId captainId;

  public Offer(final BookingId bookingId,
      final CaptainId captainId) {
    this.bookingId = bookingId;
    this.captainId = captainId;
  }
}
