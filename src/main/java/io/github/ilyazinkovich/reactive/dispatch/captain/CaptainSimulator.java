package io.github.ilyazinkovich.reactive.dispatch.captain;

import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.function.Supplier;

public class CaptainSimulator implements Consumer<Offer> {

  private final PublishSubject<CaptainResponse> captainResponseSubject;
  private final Supplier<Boolean> behaviour;

  public CaptainSimulator(final PublishSubject<CaptainResponse> captainResponseSubject,
      final Supplier<Boolean> behaviour) {
    this.captainResponseSubject = captainResponseSubject;
    this.behaviour = behaviour;
  }

  public void subscribe(final Consumer<CaptainResponse> consumer) {
    captainResponseSubject.subscribe(consumer);
  }

  @Override
  public void accept(final Offer offer) {
    final CaptainResponse captainResponse =
        new CaptainResponse(offer.booking, offer.captainId, behaviour.get());
    captainResponseSubject.onNext(captainResponse);
  }
}
