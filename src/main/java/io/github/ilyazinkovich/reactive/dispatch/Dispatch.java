package io.github.ilyazinkovich.reactive.dispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.RedispatchRequired;
import io.github.ilyazinkovich.reactive.dispatch.filter.Filter;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offers;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.Redispatch;
import io.github.ilyazinkovich.reactive.dispatch.sort.Sort;
import io.github.ilyazinkovich.reactive.dispatch.supply.Supply;
import reactor.core.publisher.Mono;

public class Dispatch {

  private final Supply supply;
  private final Filter filter;
  private final Sort sort;
  private final Offers offers;
  private final Redispatch redispatch;

  public Dispatch(final Supply supply, final Filter filter, final Sort sort, final Offers offers,
      final Redispatch redispatch) {
    this.supply = supply;
    this.filter = filter;
    this.sort = sort;
    this.offers = offers;
    this.redispatch = redispatch;
  }

  public Mono<Offer> dispatch(final Booking booking) {
    return Mono.just(booking)
        .flatMap(supply::accept)
        .flatMap(filter::accept)
        .flatMap(sort::accept)
        .flatMap(offers::accept)
        .onErrorResume(RedispatchRequired.class,
            error -> redispatch.scheduleReDispatch(error.booking).flatMap(this::dispatch));
  }
}
