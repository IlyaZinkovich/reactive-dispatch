package io.github.ilyazinkovich.reactive.dispatch.core;

public class Booking {

  public final BookingId id;
  public final Location pickupLocation;

  public Booking(final BookingId id, final Location pickupLocation) {
    this.id = id;
    this.pickupLocation = pickupLocation;
  }
}
