package io.github.ilyazinkovich.reactive.dispatch.captain;

import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.Random;

public class CaptainSimulator implements Consumer<Offer> {

  private final Random random = new Random();
  private final PublishSubject<CaptainResponse> captainResponseSubject;

  public CaptainSimulator(final PublishSubject<CaptainResponse> captainResponseSubject) {
    this.captainResponseSubject = captainResponseSubject;
  }

  public void subscribe(final Consumer<CaptainResponse> consumer) {
    captainResponseSubject.subscribe(consumer);
  }

  @Override
  public void accept(final Offer offer) {
    final CaptainResponse captainResponse =
        new CaptainResponse(offer.booking, offer.captainId, random.nextBoolean());
    captainResponseSubject.onNext(captainResponse);
  }
}
