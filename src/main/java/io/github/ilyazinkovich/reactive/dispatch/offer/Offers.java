package io.github.ilyazinkovich.reactive.dispatch.offer;

import io.github.ilyazinkovich.reactive.dispatch.sort.SortedCaptains;
import java.util.function.Consumer;

public class Offers {

  private final Consumer<Offer> offersSubject;

  public Offers(final Consumer<Offer> offersSubject) {
    this.offersSubject = offersSubject;
  }

  public void accept(final SortedCaptains sortedCaptains) {
    sortedCaptains.captains.stream().findFirst()
        .map(captain -> new Offer(sortedCaptains.booking, captain.id))
        .ifPresent(offersSubject);
  }
}
