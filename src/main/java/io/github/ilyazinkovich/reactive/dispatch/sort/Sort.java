package io.github.ilyazinkovich.reactive.dispatch.sort;

import static java.util.stream.Collectors.toList;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.filter.FilteredCaptains;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.List;

public class Sort implements Consumer<FilteredCaptains> {

  private final PublishSubject<SortedCaptains> sortedCaptainsSubject;

  public Sort(final PublishSubject<SortedCaptains> sortedCaptainsSubject) {
    this.sortedCaptainsSubject = sortedCaptainsSubject;
  }

  @Override
  public void accept(final FilteredCaptains filteredCaptains) {
    final List<Captain> captains = filteredCaptains.captains.stream()
        .sorted(Captain.comparator()).collect(toList());
    final SortedCaptains sortedCaptains =
        new SortedCaptains(filteredCaptains.booking, captains);
    sortedCaptainsSubject.onNext(sortedCaptains);
  }

  public void subscribe(final Consumer<SortedCaptains> consumer) {
    sortedCaptainsSubject.subscribe(consumer);
  }
}
