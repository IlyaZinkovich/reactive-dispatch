package io.github.ilyazinkovich.reactive.dispatch;

import static io.reactivex.BackpressureStrategy.BUFFER;
import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import io.github.ilyazinkovich.reactive.dispatch.assignment.Assignments;
import io.github.ilyazinkovich.reactive.dispatch.captain.CaptainSimulator;
import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.BookingId;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.CaptainId;
import io.github.ilyazinkovich.reactive.dispatch.core.Location;
import io.github.ilyazinkovich.reactive.dispatch.filter.Filter;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offers;
import io.github.ilyazinkovich.reactive.dispatch.offer.ReDispatch;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.ReDispatcher;
import io.github.ilyazinkovich.reactive.dispatch.sort.Sort;
import io.github.ilyazinkovich.reactive.dispatch.supply.Supply;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class IntegrationTest {

  private final Random random = new Random();

  @Test
  void test() throws InterruptedException {
    final int bookingsCount = 10;
    final List<Booking> bookings = Stream.generate(this::randomBooking)
        .limit(bookingsCount).collect(toList());
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation = bookings.stream()
        .collect(toConcurrentMap(booking -> booking.pickupLocation, booking -> randomCaptains()));
    final PublishSubject<ReDispatch> reDispatchesSubject = PublishSubject.create();
    final PublishSubject<Booking> bookingsSubject = PublishSubject.create();
    final ReDispatcher reDispatcher =
        new ReDispatcher(bookingsSubject, reDispatchesSubject.toFlowable(BUFFER));
    final Supply supply = new Supply(bookingsSubject.toFlowable(BUFFER), captainsByLocation);
    final Filter filter = new Filter(supply.suppliedCaptainsStream);
    final Sort sort = new Sort(filter.filteredCaptainsStream);
    final Offers offers = new Offers(sort.sortedCaptainsStream, reDispatchesSubject);
    final CaptainSimulator captainSimulator = new CaptainSimulator(offers.offersStream);
    final Assignments assignments =
        new Assignments(captainSimulator.captainResponseStream, reDispatchesSubject);
    assignments.assignmentsStream.subscribe(assignment ->
        System.out.printf("Assigned captain %s to booking %s%n",
            assignment.captainId, assignment.booking.id));
    Thread.sleep(10000);
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
