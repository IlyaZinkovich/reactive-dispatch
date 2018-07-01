package io.github.ilyazinkovich.reactive.dispatch;

import static java.util.Collections.emptySet;

import io.reactivex.Flowable;
import java.util.Map;
import java.util.Set;

public class Supply {

  private final Map<Location, Set<Captain>> captainsByLocation;
  final Flowable<SuppliedCaptains> suppliedCaptains;

  public Supply(final Flowable<Booking> bookings,
      final Map<Location, Set<Captain>> captainsByLocation) {
    this.captainsByLocation = captainsByLocation;
    this.suppliedCaptains = bookings.map(this::supplyCaptainsForBooking);
  }

  private SuppliedCaptains supplyCaptainsForBooking(final Booking booking) {
    final Set<Captain> captains =
        captainsByLocation.getOrDefault(booking.pickupLocation, emptySet());
    return new SuppliedCaptains(booking.id, captains);
  }
}
