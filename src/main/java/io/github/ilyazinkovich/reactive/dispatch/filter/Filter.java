package io.github.ilyazinkovich.reactive.dispatch.filter;

import static java.util.stream.Collectors.toSet;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.RedispatchRequired;
import io.github.ilyazinkovich.reactive.dispatch.supply.SuppliedCaptains;
import java.util.Set;
import java.util.function.Predicate;
import reactor.core.publisher.Mono;

public class Filter {

  private final Predicate<Captain> captainFilter;

  public Filter(final Predicate<Captain> captainFilter) {
    this.captainFilter = captainFilter;
  }

  public Mono<FilteredCaptains> accept(final SuppliedCaptains suppliedCaptains) {
    final Set<Captain> captains = suppliedCaptains.captains.stream()
        .filter(captainFilter)
        .collect(toSet());
    if (captains.isEmpty()) {
      return Mono.error(new RedispatchRequired(suppliedCaptains.booking));
    } else {
      final FilteredCaptains filteredCaptains =
          new FilteredCaptains(suppliedCaptains.booking, captains);
      return Mono.just(filteredCaptains);
    }
  }
}
