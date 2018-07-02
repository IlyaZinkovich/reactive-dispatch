package io.github.ilyazinkovich.reactive.dispatch.sort;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import java.util.List;

public class SortedCaptains {

  public final List<Captain> captains;
  public final Booking booking;

  SortedCaptains(final Booking booking, final List<Captain> captains) {
    this.booking = booking;
    this.captains = captains;
  }
}
