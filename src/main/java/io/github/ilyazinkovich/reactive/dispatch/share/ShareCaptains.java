package io.github.ilyazinkovich.reactive.dispatch.share;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.supply.SuppliedCaptains;
import java.util.List;
import java.util.Set;

public class ShareCaptains {

  public List<SuppliedCaptains> share(final List<SuppliedCaptains> suppliedCaptains) {
    final Set<Captain> allCaptains = suppliedCaptains.stream()
        .flatMap(captainsPerBooking -> captainsPerBooking.captains.stream()).collect(toSet());
    return suppliedCaptains.stream()
        .map(captainsPerBooking -> new SuppliedCaptains(captainsPerBooking.booking, allCaptains))
        .collect(toList());
  }
}
