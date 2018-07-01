package io.github.ilyazinkovich.reactive.dispatch;

import static java.util.stream.Collectors.toList;

import io.reactivex.Flowable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class IntegrationTest {

  private final Random random = new Random();

  @Test
  void test() {
    final ConcurrentHashMap<Location, Set<Captain>> captainsByLocation = new ConcurrentHashMap<>();
    final List<Booking> bookings = Stream.generate(this::randomBooking).limit(10).collect(toList());
    final Supply supply = new Supply(Flowable.fromIterable(bookings), captainsByLocation);
    final Filter filter = new Filter(supply.suppliedCaptains);
    final Sort sort = new Sort(filter.filteredCaptains);
    final Offers offers = new Offers(sort.sortedCaptains);
    sort.sortedCaptains.test().assertValueCount(10);
  }

  private Booking randomBooking() {
    return new Booking(BookingId.next(), randomLocation());
  }

  private Location randomLocation() {
    return new Location(randomAngle(), randomAngle());
  }

  private double randomAngle() {
    return random.nextDouble() * 180 - 90;
  }
}
