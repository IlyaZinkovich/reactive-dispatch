package io.github.ilyazinkovich.reactive.dispatch.filter;

import static java.util.stream.Collectors.toSet;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.ReDispatch;
import io.github.ilyazinkovich.reactive.dispatch.supply.SuppliedCaptains;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.Set;
import java.util.function.Predicate;

public class Filter implements Consumer<SuppliedCaptains> {

  private final PublishSubject<FilteredCaptains> filteredCaptainsSubject;
  private final Predicate<Captain> captainFilter;
  private final PublishSubject<ReDispatch> reDispatchSubject;

  public Filter(final PublishSubject<FilteredCaptains> filteredCaptainsSubject,
      final Predicate<Captain> captainFilter,
      final PublishSubject<ReDispatch> reDispatchSubject) {
    this.filteredCaptainsSubject = filteredCaptainsSubject;
    this.captainFilter = captainFilter;
    this.reDispatchSubject = reDispatchSubject;
  }

  @Override
  public void accept(final SuppliedCaptains suppliedCaptains) {
    final Set<Captain> captains = suppliedCaptains.captains.stream()
        .filter(captainFilter)
        .collect(toSet());
    if (captains.isEmpty()) {
      reDispatchSubject.onNext(new ReDispatch(suppliedCaptains.booking));
    } else {
      final FilteredCaptains filteredCaptains =
          new FilteredCaptains(suppliedCaptains.booking, captains);
      filteredCaptainsSubject.onNext(filteredCaptains);
    }
  }

  public void subscribe(final Consumer<FilteredCaptains> consumer) {
    filteredCaptainsSubject.subscribe(consumer);
  }
}
