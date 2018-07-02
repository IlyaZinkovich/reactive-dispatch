package io.github.ilyazinkovich.reactive.dispatch.supply;

import static java.util.Collections.emptySet;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.Location;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Supply {

  private final Consumer<SuppliedCaptains> suppliedCaptainsSubject;
  private final Map<Location, Set<Captain>> captainsByLocation;

  public Supply(final Consumer<SuppliedCaptains> suppliedCaptainsSubject,
      final Map<Location, Set<Captain>> captainsByLocation) {
    this.suppliedCaptainsSubject = suppliedCaptainsSubject;
    this.captainsByLocation = captainsByLocation;
  }

  public void accept(final Booking booking) {
    final Set<Captain> captains =
        captainsByLocation.getOrDefault(booking.pickupLocation, emptySet());
    suppliedCaptainsSubject.accept(new SuppliedCaptains(booking, captains));
  }
}
