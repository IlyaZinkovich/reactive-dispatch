package io.github.ilyazinkovich.reactive.dispatch;

import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;

public class Offers {

  final Flowable<Offer> offers;
  final Flowable<ReDispatch> reDispatches;

  public Offers(final Flowable<SortedCaptains> sortedCaptains) {
    final ConnectableFlowable<SortedCaptains> stream = sortedCaptains.publish();
    this.offers = stream.filter(captains -> !captains.sortedCaptains.isEmpty())
        .map(this::offer);
    this.reDispatches = stream.filter(captains -> captains.sortedCaptains.isEmpty())
        .map(this::reDispatch);
  }

  private Offer offer(final SortedCaptains captains) {
    return new Offer(captains.bookingId, captains.sortedCaptains.get(0).id);
  }

  private ReDispatch reDispatch(final SortedCaptains sortedCaptains) {
    return new ReDispatch(sortedCaptains.bookingId);
  }
}
