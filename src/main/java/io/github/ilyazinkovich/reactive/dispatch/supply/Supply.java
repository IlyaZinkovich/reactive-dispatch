package io.github.ilyazinkovich.reactive.dispatch.supply;

import static java.util.Collections.emptySet;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.Location;
import io.reactivex.Flowable;
import java.util.Map;
import java.util.Set;

public class Supply {

  public final Flowable<SuppliedCaptains> suppliedCaptainsStream;
  private final Map<Location, Set<Captain>> captainsByLocation;

  public Supply(final Flowable<Booking> bookings,
      final Map<Location, Set<Captain>> captainsByLocation) {
    this.captainsByLocation = captainsByLocation;
    this.suppliedCaptainsStream = bookings.map(this::supplyCaptainsForBooking);
  }

  private SuppliedCaptains supplyCaptainsForBooking(final Booking booking) {
    final Set<Captain> captains =
        captainsByLocation.getOrDefault(booking.pickupLocation, emptySet());
    return new SuppliedCaptains(booking, captains);
  }
}
