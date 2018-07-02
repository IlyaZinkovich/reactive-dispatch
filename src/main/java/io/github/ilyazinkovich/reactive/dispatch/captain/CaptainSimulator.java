package io.github.ilyazinkovich.reactive.dispatch.captain;

import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import io.reactivex.Flowable;
import java.util.Random;

public class CaptainSimulator {

  private final Random random = new Random();
  public final Flowable<CaptainResponse> captainResponseStream;

  public CaptainSimulator(final Flowable<Offer> offersStream) {
    this.captainResponseStream = offersStream.map(offer ->
        new CaptainResponse(offer.booking, offer.captainId, random.nextBoolean()));
  }
}
