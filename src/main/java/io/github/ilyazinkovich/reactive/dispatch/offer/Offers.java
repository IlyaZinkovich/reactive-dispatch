package io.github.ilyazinkovich.reactive.dispatch.offer;

import io.github.ilyazinkovich.reactive.dispatch.sort.SortedCaptains;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class Offers implements Consumer<SortedCaptains> {

  private final PublishSubject<Offer> offersSubject;

  public Offers(final PublishSubject<Offer> offersSubject) {
    this.offersSubject = offersSubject;
  }

  public void subscribe(final Consumer<Offer> consumer) {
    offersSubject.subscribe(consumer);
  }

  @Override
  public void accept(final SortedCaptains sortedCaptains) {
    sortedCaptains.captains.stream().findFirst()
        .map(captain -> new Offer(sortedCaptains.booking, captain.id))
        .ifPresent(offersSubject::onNext);
  }
}
