package io.github.ilyazinkovich.reactive.dispatch;

import io.github.ilyazinkovich.reactive.dispatch.buffer.Buffer;
import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.cost.CostFunction;
import io.github.ilyazinkovich.reactive.dispatch.filter.Filter;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offers;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.Redispatch;
import io.github.ilyazinkovich.reactive.dispatch.share.ShareCaptains;
import io.github.ilyazinkovich.reactive.dispatch.supply.Supply;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BucketedDispatch {

  private final Buffer buffer;
  private final Supply supply;
  private final ShareCaptains shareCaptains;
  private final Filter filter;
  private final CostFunction costFunction;
  private final Offers offers;
  private final Redispatch redispatch;

  public BucketedDispatch(final Buffer buffer,
      final Supply supply,
      final ShareCaptains shareCaptains,
      final Filter filter,
      final CostFunction costFunction,
      final Offers offers,
      final Redispatch redispatch) {
    this.buffer = buffer;
    this.supply = supply;
    this.shareCaptains = shareCaptains;
    this.filter = filter;
    this.costFunction = costFunction;
    this.offers = offers;
    this.redispatch = redispatch;
  }

  public Flux<Offer> dispatch(final Flux<Booking> bookings) {
    return buffer.formBucket(bookings)
        .map(bucket -> bucket.stream().map(supply::accept)
            .map(Mono::flux).reduce(Flux.empty(), Flux::merge))
        .map(shareCaptains::share)
        .map(bucket -> bucket.flatMap(filter::accept))
        .map(costFunction::optimiseCost)
        .flatMap(bucket -> bucket.flatMap(offers::accept));
  }
}
