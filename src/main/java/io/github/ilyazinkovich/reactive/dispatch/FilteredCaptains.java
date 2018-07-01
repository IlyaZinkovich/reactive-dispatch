package io.github.ilyazinkovich.reactive.dispatch;

import java.util.Set;

public class FilteredCaptains {

  final BookingId bookingId;
  final Set<Captain> captains;

  public FilteredCaptains(final BookingId bookingId, final Set<Captain> captains) {
    this.bookingId = bookingId;
    this.captains = captains;
  }
}
