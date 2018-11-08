package io.github.ilyazinkovich.reactive.dispatch.supply;

import static java.util.Collections.emptySet;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.Location;
import io.github.ilyazinkovich.reactive.dispatch.core.RedispatchRequiredException;
import java.util.Map;
import java.util.Set;
import reactor.core.publisher.Mono;

public class Supply {

  private final Map<Location, Set<Captain>> captainsByLocation;

  public Supply(final Map<Location, Set<Captain>> captainsByLocation) {
    this.captainsByLocation = captainsByLocation;
  }

  public Mono<SuppliedCaptains> accept(final Booking booking) {
    final Set<Captain> captains =
        captainsByLocation.getOrDefault(booking.pickupLocation, emptySet());
    if (captains.isEmpty()) {
      throw new RedispatchRequiredException(booking);
    } else {
      return Mono.just(new SuppliedCaptains(booking, captains));
    }
  }
}
