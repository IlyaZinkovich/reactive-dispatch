package io.github.ilyazinkovich.reactive.dispatch;

import static java.util.stream.Collectors.toSet;

import io.reactivex.Flowable;
import java.util.Random;
import java.util.Set;

public class Filter {

  final Flowable<FilteredCaptains> filteredCaptains;
  private final Random random = new Random();

  public Filter(final Flowable<SuppliedCaptains> suppliedCaptains) {
    this.filteredCaptains = suppliedCaptains.map(this::filterCaptains);
  }

  private FilteredCaptains filterCaptains(final SuppliedCaptains supply) {
    final Set<Captain> filteredCaptains = supply.captains.stream()
        .filter(captain -> random.nextBoolean())
        .collect(toSet());
    return new FilteredCaptains(supply.bookingId, filteredCaptains);
  }
}
