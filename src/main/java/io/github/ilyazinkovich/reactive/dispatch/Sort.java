package io.github.ilyazinkovich.reactive.dispatch;

import static java.util.stream.Collectors.toList;

import io.reactivex.Flowable;
import java.util.List;

public class Sort {

  final Flowable<SortedCaptains> sortedCaptains;

  public Sort(final Flowable<FilteredCaptains> filteredCaptains) {
    this.sortedCaptains = filteredCaptains.map(this::sortCaptains);
  }

  private SortedCaptains sortCaptains(final FilteredCaptains filteredCaptains) {
    final List<Captain> sortedCaptains = filteredCaptains.captains.stream()
        .sorted(Captain.comparator()).collect(toList());
    return new SortedCaptains(filteredCaptains.bookingId, sortedCaptains);
  }
}
