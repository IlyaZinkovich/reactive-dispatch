package io.github.ilyazinkovich.reactive.dispatch.filter;

import static java.util.stream.Collectors.toSet;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.ReDispatch;
import io.github.ilyazinkovich.reactive.dispatch.supply.SuppliedCaptains;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Filter {

  private final Consumer<FilteredCaptains> filteredCaptainsSubject;
  private final Predicate<Captain> captainFilter;
  private final Consumer<ReDispatch> reDispatchSubject;

  public Filter(final Consumer<FilteredCaptains> filteredCaptainsSubject,
      final Predicate<Captain> captainFilter,
      final Consumer<ReDispatch> reDispatchSubject) {
    this.filteredCaptainsSubject = filteredCaptainsSubject;
    this.captainFilter = captainFilter;
    this.reDispatchSubject = reDispatchSubject;
  }

  public void accept(final SuppliedCaptains suppliedCaptains) {
    final Set<Captain> captains = suppliedCaptains.captains.stream()
        .filter(captainFilter)
        .collect(toSet());
    if (captains.isEmpty()) {
      reDispatchSubject.accept(new ReDispatch(suppliedCaptains.booking));
    } else {
      final FilteredCaptains filteredCaptains =
          new FilteredCaptains(suppliedCaptains.booking, captains);
      filteredCaptainsSubject.accept(filteredCaptains);
    }
  }
}
