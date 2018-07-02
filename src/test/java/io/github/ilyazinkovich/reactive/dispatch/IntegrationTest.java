package io.github.ilyazinkovich.reactive.dispatch;

import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import io.github.ilyazinkovich.reactive.dispatch.assignment.Assignment;
import io.github.ilyazinkovich.reactive.dispatch.assignment.Assignments;
import io.github.ilyazinkovich.reactive.dispatch.captain.CaptainResponse;
import io.github.ilyazinkovich.reactive.dispatch.captain.CaptainSimulator;
import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.BookingId;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.CaptainId;
import io.github.ilyazinkovich.reactive.dispatch.core.Location;
import io.github.ilyazinkovich.reactive.dispatch.filter.Filter;
import io.github.ilyazinkovich.reactive.dispatch.filter.FilteredCaptains;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offers;
import io.github.ilyazinkovich.reactive.dispatch.offer.ReDispatch;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.ReDispatcher;
import io.github.ilyazinkovich.reactive.dispatch.sort.Sort;
import io.github.ilyazinkovich.reactive.dispatch.sort.SortedCaptains;
import io.github.ilyazinkovich.reactive.dispatch.supply.SuppliedCaptains;
import io.github.ilyazinkovich.reactive.dispatch.supply.Supply;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class IntegrationTest {

  private final Random random = new Random();
  private final PublishSubject<Booking> bookingsSubject = PublishSubject.create();
  private final PublishSubject<ReDispatch> reDispatchesSubject = PublishSubject.create();
  private final PublishSubject<SuppliedCaptains> suppliedCaptainsSubject = PublishSubject.create();
  private final PublishSubject<FilteredCaptains> filteredCaptainsSubject = PublishSubject.create();
  private final PublishSubject<SortedCaptains> sortedCaptainsSubject = PublishSubject.create();
  private final PublishSubject<Offer> offersSubject = PublishSubject.create();
  private final PublishSubject<CaptainResponse> captainResponseSubject = PublishSubject.create();
  private final PublishSubject<Assignment> assignmentsSubject = PublishSubject.create();

  @Test
  void test() {
    final int bookingsCount = 10;
    final List<Booking> bookings = Stream.generate(this::randomBooking)
        .limit(bookingsCount).collect(toList());
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation = bookings.stream()
        .collect(toConcurrentMap(booking -> booking.pickupLocation, booking -> randomCaptains()));
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    final ReDispatcher reDispatcher = new ReDispatcher(bookingsSubject, retriesCount);
    reDispatchesSubject.subscribe(reDispatcher);
    final Supply supply = new Supply(suppliedCaptainsSubject, captainsByLocation);
    bookingsSubject.subscribe(supply);
    final Filter filter = new Filter(filteredCaptainsSubject);
    supply.subscribe(filter);
    final Sort sort = new Sort(sortedCaptainsSubject);
    filter.subscribe(sort);
    final Offers offers = new Offers(offersSubject, reDispatchesSubject);
    sort.subscribe(offers);
    final CaptainSimulator captainSimulator = new CaptainSimulator(captainResponseSubject);
    offers.subscribeOffers(captainSimulator);
    offers.subscribeReDispatches(reDispatcher);
    final Assignments assignments = new Assignments(assignmentsSubject, reDispatchesSubject);
    captainSimulator.subscribe(assignments);
    assignments.subscribe(assignment ->
        System.out.printf("Assigned captain %s to booking %s%n",
            assignment.captainId, assignment.booking.id));
    bookings.forEach(bookingsSubject::onNext);
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
        .limit(random.nextInt(8) + 1)
        .collect(toSet());
  }

  private Captain randomCaptain() {
    return new Captain(CaptainId.next());
  }
}
