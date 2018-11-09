package io.github.ilyazinkovich.reactive.dispatch.supply;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import java.util.Set;

public class SuppliedCaptains {

  public final Booking booking;
  public final Set<Captain> captains;

  public SuppliedCaptains(final Booking booking, final Set<Captain> captains) {
    this.booking = booking;
    this.captains = captains;
  }
}
