package io.github.ilyazinkovich.reactive.dispatch.offer;

import io.github.ilyazinkovich.reactive.dispatch.sort.SortedCaptains;
import reactor.core.publisher.Mono;

public class Offers {

  public Mono<Offer> accept(final SortedCaptains sortedCaptains) {
    return sortedCaptains.captains.stream().findFirst()
        .map(captain -> new Offer(sortedCaptains.booking, captain.id))
        .map(Mono::just).orElseGet(Mono::empty);
  }
}
