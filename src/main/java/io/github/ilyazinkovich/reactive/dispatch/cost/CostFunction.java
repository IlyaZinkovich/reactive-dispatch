package io.github.ilyazinkovich.reactive.dispatch.cost;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.filter.FilteredCaptains;
import io.github.ilyazinkovich.reactive.dispatch.sort.SortedCaptains;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import reactor.core.publisher.Flux;

public class CostFunction {

  public Flux<SortedCaptains> optimiseCost(
      final Flux<FilteredCaptains> availableCaptainsPerBooking) {
    return availableCaptainsPerBooking.map(filteredCaptains -> {
      final List<Captain> captains = new ArrayList<>(filteredCaptains.captains);
      Collections.shuffle(captains);
      return new SortedCaptains(filteredCaptains.booking, captains);
    });
  }
}
