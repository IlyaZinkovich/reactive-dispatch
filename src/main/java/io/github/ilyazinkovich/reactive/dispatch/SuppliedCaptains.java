package io.github.ilyazinkovich.reactive.dispatch;

import java.util.Set;

public class SuppliedCaptains {

  final BookingId bookingId;
  final Set<Captain> captains;

  public SuppliedCaptains(final BookingId bookingId, final Set<Captain> captains) {
    this.bookingId = bookingId;
    this.captains = captains;
  }
}
