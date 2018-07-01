package io.github.ilyazinkovich.reactive.dispatch;

import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.reactivex.Flowable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class IntegrationTest {

  private final Random random = new Random();

  @Test
  void test() {
    final int bookingsCount = 10;
    final List<Booking> bookings = Stream.generate(this::randomBooking)
        .limit(bookingsCount).collect(toList());
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation = bookings.stream()
        .collect(toConcurrentMap(booking -> booking.pickupLocation, booking -> randomCaptains()));
    final Supply supply = new Supply(Flowable.fromIterable(bookings), captainsByLocation);
    final Filter filter = new Filter(supply.suppliedCaptains);
    final Sort sort = new Sort(filter.filteredCaptains);
    final Offers offers = new Offers(sort.sortedCaptains);
    final int offersCount = offers.offers.test().valueCount();
    final int reDispatchesCount = offers.reDispatches.test().valueCount();
    assertEquals(bookingsCount, offersCount + reDispatchesCount);
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

  private Set<Captain> randomCaptains() {
    return Stream.generate(this::randomCaptain)
        .limit(random.nextInt(8))
        .collect(toSet());
  }

  private Captain randomCaptain() {
    return new Captain(CaptainId.next());
  }
}
