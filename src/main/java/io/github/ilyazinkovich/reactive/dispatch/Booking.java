package io.github.ilyazinkovich.reactive.dispatch;

public class Booking {

  final BookingId id;
  final Location pickupLocation;

  public Booking(final BookingId id, final Location pickupLocation) {
    this.id = id;
    this.pickupLocation = pickupLocation;
  }
}
