package io.github.ilyazinkovich.reactive.dispatch;

import java.util.List;

public class SortedCaptains {

  final BookingId bookingId;
  final List<Captain> sortedCaptains;

  public SortedCaptains(final BookingId bookingId,
      final List<Captain> sortedCaptains) {
    this.bookingId = bookingId;
    this.sortedCaptains = sortedCaptains;
  }
}
