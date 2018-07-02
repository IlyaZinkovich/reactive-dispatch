package io.github.ilyazinkovich.reactive.dispatch.offer;

import static io.reactivex.BackpressureStrategy.BUFFER;

import io.github.ilyazinkovich.reactive.dispatch.sort.SortedCaptains;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class Offers {

  public final Flowable<Offer> offersStream;

  public Offers(final Flowable<SortedCaptains> sortedCaptainsStream,
      final PublishSubject<ReDispatch> reDispatchesSubject) {
    final PublishSubject<Offer> offersSubject = PublishSubject.create();
    sortedCaptainsStream.doOnNext(sortedCaptains -> {
      if (sortedCaptains.captains.isEmpty()) {
        offersSubject.onNext(offer(sortedCaptains));
      } else {
        reDispatchesSubject.onNext(reDispatch(sortedCaptains));
      }
    });
    this.offersStream = offersSubject.toFlowable(BUFFER);
  }

  private Offer offer(final SortedCaptains captains) {
    return new Offer(captains.booking, captains.captains.get(0).id);
  }

  private ReDispatch reDispatch(final SortedCaptains captains) {
    return new ReDispatch(captains.booking);
  }
}
