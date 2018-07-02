package io.github.ilyazinkovich.reactive.dispatch.filter;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import java.util.Set;

public class FilteredCaptains {

  public final Booking booking;
  public final Set<Captain> captains;

  public FilteredCaptains(final Booking booking,
      final Set<Captain> captains) {
    this.booking = booking;
    this.captains = captains;
  }
}
