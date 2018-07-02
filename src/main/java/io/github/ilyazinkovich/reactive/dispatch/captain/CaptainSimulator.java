package io.github.ilyazinkovich.reactive.dispatch.captain;

import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CaptainSimulator {

  private final Consumer<CaptainResponse> captainResponseConsumer;
  private final Supplier<Boolean> behaviour;

  public CaptainSimulator(final Consumer<CaptainResponse> captainResponseConsumer,
      final Supplier<Boolean> behaviour) {
    this.captainResponseConsumer = captainResponseConsumer;
    this.behaviour = behaviour;
  }

  public void accept(final Offer offer) {
    final CaptainResponse captainResponse =
        new CaptainResponse(offer.booking, offer.captainId, behaviour.get());
    captainResponseConsumer.accept(captainResponse);
  }
}
