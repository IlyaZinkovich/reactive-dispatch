package io.github.ilyazinkovich.reactive.dispatch.supply;

import static java.util.Collections.emptySet;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.Location;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.Map;
import java.util.Set;

public class Supply implements Consumer<Booking> {

  private final PublishSubject<SuppliedCaptains> suppliedCaptainsSubject;
  private final Map<Location, Set<Captain>> captainsByLocation;

  public Supply(final PublishSubject<SuppliedCaptains> suppliedCaptainsSubject,
      final Map<Location, Set<Captain>> captainsByLocation) {
    this.suppliedCaptainsSubject = suppliedCaptainsSubject;
    this.captainsByLocation = captainsByLocation;
  }

  @Override
  public void accept(final Booking booking) {
    final Set<Captain> captains =
        captainsByLocation.getOrDefault(booking.pickupLocation, emptySet());
    suppliedCaptainsSubject.onNext(new SuppliedCaptains(booking, captains));
  }

  public void subscribe(final Consumer<SuppliedCaptains> consumer) {
    suppliedCaptainsSubject.subscribe(consumer);
  }
}
