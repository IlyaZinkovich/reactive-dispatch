package io.github.ilyazinkovich.reactive.dispatch.cost;

import io.github.ilyazinkovich.reactive.dispatch.filter.FilteredCaptains;
import io.github.ilyazinkovich.reactive.dispatch.sort.SortedCaptains;
import java.util.ArrayList;
import reactor.core.publisher.Flux;

public class CostFunction {

  public Flux<SortedCaptains> optimiseCost(
      final Flux<FilteredCaptains> availableCaptainsPerBooking) {
    return availableCaptainsPerBooking.map(filteredCaptains ->
        new SortedCaptains(filteredCaptains.booking, new ArrayList<>(filteredCaptains.captains)));
  }
}
