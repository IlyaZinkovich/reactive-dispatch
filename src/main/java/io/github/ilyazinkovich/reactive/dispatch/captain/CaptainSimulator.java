package io.github.ilyazinkovich.reactive.dispatch.captain;

import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import java.util.function.Supplier;
import reactor.core.publisher.Mono;

public class CaptainSimulator {

  private final Supplier<Boolean> behaviour;

  public CaptainSimulator(final Supplier<Boolean> behaviour) {
    this.behaviour = behaviour;
  }

  public Mono<CaptainResponse> accept(final Offer offer) {
    final CaptainResponse captainResponse =
        new CaptainResponse(offer.booking, offer.captainId, behaviour.get());
    return Mono.just(captainResponse);
  }
}
