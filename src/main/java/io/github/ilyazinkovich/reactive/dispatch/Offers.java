package io.github.ilyazinkovich.reactive.dispatch;

import io.reactivex.Flowable;

public class Offers {

  final Flowable<Offer> offers;
  final Flowable<ReDispatch> reDispatches;

  public Offers(final Flowable<SortedCaptains> sortedCaptains) {
    final Flowable<SortedCaptains> stream = sortedCaptains.share();
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
