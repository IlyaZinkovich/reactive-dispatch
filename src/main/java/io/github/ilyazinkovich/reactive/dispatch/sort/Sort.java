package io.github.ilyazinkovich.reactive.dispatch.sort;

import static java.util.stream.Collectors.toList;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.filter.FilteredCaptains;
import io.reactivex.Flowable;
import java.util.List;

public class Sort {

  public final Flowable<SortedCaptains> sortedCaptainsStream;

  public Sort(final Flowable<FilteredCaptains> filteredCaptains) {
    this.sortedCaptainsStream = filteredCaptains.map(this::sortCaptains);
  }

  private SortedCaptains sortCaptains(final FilteredCaptains filteredCaptains) {
    final List<Captain> sortedCaptains = filteredCaptains.captains.stream()
        .sorted(Captain.comparator()).collect(toList());
    return new SortedCaptains(filteredCaptains.booking, sortedCaptains);
  }
}
