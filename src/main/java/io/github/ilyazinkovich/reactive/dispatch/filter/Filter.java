package io.github.ilyazinkovich.reactive.dispatch.filter;

import static java.util.stream.Collectors.toSet;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.supply.SuppliedCaptains;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.Random;
import java.util.Set;

public class Filter implements Consumer<SuppliedCaptains> {

  private final PublishSubject<FilteredCaptains> filteredCaptainsSubject;
  private final Random random = new Random();

  public Filter(final PublishSubject<FilteredCaptains> filteredCaptainsSubject) {
    this.filteredCaptainsSubject = filteredCaptainsSubject;
  }

  @Override
  public void accept(final SuppliedCaptains suppliedCaptains) {
    final Set<Captain> captains = suppliedCaptains.captains.stream()
        .filter(captain -> true)
        .collect(toSet());
    final FilteredCaptains filteredCaptains =
        new FilteredCaptains(suppliedCaptains.booking, captains);
    filteredCaptainsSubject.onNext(filteredCaptains);
  }

  public void subscribe(final Consumer<FilteredCaptains> consumer) {
    filteredCaptainsSubject.subscribe(consumer);
  }
}
