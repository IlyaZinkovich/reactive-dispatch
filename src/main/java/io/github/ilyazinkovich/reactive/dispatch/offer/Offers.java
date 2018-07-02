package io.github.ilyazinkovich.reactive.dispatch.offer;

import io.github.ilyazinkovich.reactive.dispatch.redispatch.ReDispatch;
import io.github.ilyazinkovich.reactive.dispatch.sort.SortedCaptains;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class Offers implements Consumer<SortedCaptains> {

  private final PublishSubject<Offer> offersSubject;
  private final PublishSubject<ReDispatch> reDispatchesSubject;

  public Offers(final PublishSubject<Offer> offersSubject,
      final PublishSubject<ReDispatch> reDispatchesSubject) {
    this.offersSubject = offersSubject;
    this.reDispatchesSubject = reDispatchesSubject;
  }

  public void subscribeOffers(final Consumer<Offer> offerConsumer) {
    offersSubject.subscribe(offerConsumer);
  }

  public void subscribeReDispatches(final Consumer<ReDispatch> reDispatchConsumer) {
    reDispatchesSubject.subscribe(reDispatchConsumer);
  }

  @Override
  public void accept(final SortedCaptains sortedCaptains) {
    if (sortedCaptains.captains.isEmpty()) {
      final ReDispatch reDispatch = new ReDispatch(sortedCaptains.booking);
      reDispatchesSubject.onNext(reDispatch);
    } else {
      final Offer offer = new Offer(sortedCaptains.booking,
          sortedCaptains.captains.get(0).id);
      offersSubject.onNext(offer);
    }
  }
}
