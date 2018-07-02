package io.github.ilyazinkovich.reactive.dispatch.sort;

import static java.util.stream.Collectors.toList;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.filter.FilteredCaptains;
import java.util.List;
import java.util.function.Consumer;

public class Sort {

  private final Consumer<SortedCaptains> sortedCaptainsSubject;

  public Sort(final Consumer<SortedCaptains> sortedCaptainsSubject) {
    this.sortedCaptainsSubject = sortedCaptainsSubject;
  }

  public void accept(final FilteredCaptains filteredCaptains) {
    final List<Captain> captains = filteredCaptains.captains.stream()
        .sorted(Captain.comparator()).collect(toList());
    final SortedCaptains sortedCaptains =
        new SortedCaptains(filteredCaptains.booking, captains);
    sortedCaptainsSubject.accept(sortedCaptains);
  }
}
