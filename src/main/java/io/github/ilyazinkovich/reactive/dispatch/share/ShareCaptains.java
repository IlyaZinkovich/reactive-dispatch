package io.github.ilyazinkovich.reactive.dispatch.share;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.supply.SuppliedCaptains;
import java.util.HashSet;
import java.util.Set;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ShareCaptains {

  public Flux<SuppliedCaptains> share(final Flux<SuppliedCaptains> suppliedCaptains) {
    final Mono<Set<Captain>> allCaptains = suppliedCaptains.reduce(
        new HashSet<>(),
        (captains, captainsPerBooking) -> union(captains, captainsPerBooking.captains)
    );
    return suppliedCaptains.flatMap(captainsPerBooking ->
        allCaptains.map(captains -> new SuppliedCaptains(captainsPerBooking.booking, captains)));
  }

  private Set<Captain> union(final Set<Captain> left, final Set<Captain> right) {
    left.addAll(right);
    return left;
  }
}
