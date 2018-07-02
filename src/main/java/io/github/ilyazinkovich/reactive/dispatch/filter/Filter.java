package io.github.ilyazinkovich.reactive.dispatch.filter;

import static java.util.stream.Collectors.toSet;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.supply.SuppliedCaptains;
import io.reactivex.Flowable;
import java.util.Random;
import java.util.Set;

public class Filter {

  public final Flowable<FilteredCaptains> filteredCaptainsStream;
  private final Random random = new Random();

  public Filter(final Flowable<SuppliedCaptains> suppliedCaptains) {
    this.filteredCaptainsStream = suppliedCaptains.map(this::filterCaptains);
  }

  private FilteredCaptains filterCaptains(final SuppliedCaptains supply) {
    final Set<Captain> filteredCaptains = supply.captains.stream()
        .filter(captain -> random.nextBoolean())
        .collect(toSet());
    return new FilteredCaptains(supply.booking, filteredCaptains);
  }
}
