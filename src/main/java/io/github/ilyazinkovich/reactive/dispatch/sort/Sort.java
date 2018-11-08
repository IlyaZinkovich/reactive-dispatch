package io.github.ilyazinkovich.reactive.dispatch.sort;

import static java.util.stream.Collectors.toList;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.filter.FilteredCaptains;
import java.util.List;
import reactor.core.publisher.Mono;

public class Sort {

  public Mono<SortedCaptains> accept(final FilteredCaptains filteredCaptains) {
    final List<Captain> captains = filteredCaptains.captains.stream()
        .sorted(Captain.comparator()).collect(toList());
    final SortedCaptains sortedCaptains =
        new SortedCaptains(filteredCaptains.booking, captains);
    return Mono.just(sortedCaptains);
  }
}
