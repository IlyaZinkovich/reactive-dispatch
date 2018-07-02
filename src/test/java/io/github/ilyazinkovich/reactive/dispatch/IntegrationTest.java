package io.github.ilyazinkovich.reactive.dispatch;

import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.ilyazinkovich.reactive.dispatch.assignment.Assignment;
import io.github.ilyazinkovich.reactive.dispatch.assignment.Assignments;
import io.github.ilyazinkovich.reactive.dispatch.captain.CaptainResponse;
import io.github.ilyazinkovich.reactive.dispatch.captain.CaptainSimulator;
import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.BookingId;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.CaptainId;
import io.github.ilyazinkovich.reactive.dispatch.core.Location;
import io.github.ilyazinkovich.reactive.dispatch.core.ReDispatch;
import io.github.ilyazinkovich.reactive.dispatch.filter.Filter;
import io.github.ilyazinkovich.reactive.dispatch.filter.FilteredCaptains;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offers;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.ReDispatcher;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.RetriesExceeded;
import io.github.ilyazinkovich.reactive.dispatch.sort.Sort;
import io.github.ilyazinkovich.reactive.dispatch.sort.SortedCaptains;
import io.github.ilyazinkovich.reactive.dispatch.supply.SuppliedCaptains;
import io.github.ilyazinkovich.reactive.dispatch.supply.Supply;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class IntegrationTest {

  private static final Random random = new Random();
  private static final Predicate<Captain> RANDOM_CAPTAIN = captain -> random.nextBoolean();
  private static final Supplier<Boolean> ALWAYS_ACCEPT_OFFERS = () -> true;
  private static final Supplier<Boolean> ALWAYS_DECLINE_OFFERS = () -> false;
  private static final Supplier<Integer> AT_LEAST_ONE = () -> random.nextInt(8) + 1;
  private static final Supplier<Integer> AT_MOST_EIGHT = () -> random.nextInt(8);
  private static final Supplier<Integer> ZERO = () -> 0;
  private static final Predicate<Captain> NO_CAPTAINS_FILTER = captain -> true;
  private static final Predicate<Captain> ALL_CAPTAINS_FILTER = captain -> false;
  private final PublishSubject<Booking> bookingsSubject = PublishSubject.create();
  private final PublishSubject<ReDispatch> reDispatchSubject = PublishSubject.create();
  private final PublishSubject<SuppliedCaptains> suppliedCaptainsSubject = PublishSubject.create();
  private final PublishSubject<FilteredCaptains> filteredCaptainsSubject = PublishSubject.create();
  private final PublishSubject<SortedCaptains> sortedCaptainsSubject = PublishSubject.create();
  private final PublishSubject<Offer> offersSubject = PublishSubject.create();
  private final PublishSubject<CaptainResponse> captainResponseSubject = PublishSubject.create();
  private final PublishSubject<Assignment> assignmentsSubject = PublishSubject.create();
  private final PublishSubject<RetriesExceeded> retriesExceededSubject = PublishSubject.create();

  @Test
  void testOptimisticFlow() {
    final int bookingsCount = 10;
    final List<Booking> bookings = generateBookings(bookingsCount);
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation =
        generateCaptainsPerBooking(bookings, AT_LEAST_ONE);
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    final ReDispatcher reDispatcher =
        new ReDispatcher(bookingsSubject::onNext, retriesCount, retriesExceededSubject::onNext);
    final Supply supply = new Supply(suppliedCaptainsSubject::onNext, captainsByLocation);
    final Filter filter =
        new Filter(filteredCaptainsSubject::onNext, NO_CAPTAINS_FILTER, reDispatchSubject::onNext);
    final Sort sort = new Sort(sortedCaptainsSubject::onNext);
    final Offers offers = new Offers(offersSubject::onNext);
    final CaptainSimulator captainSimulator =
        new CaptainSimulator(captainResponseSubject::onNext, ALWAYS_ACCEPT_OFFERS);
    final Assignments assignments =
        new Assignments(assignmentsSubject::onNext, reDispatchSubject::onNext);
    wire(reDispatcher, supply, filter, sort, offers, captainSimulator, assignments);
    final TestSubscriber<Assignment> assignmentsTestSubscriber = TestSubscriber.create();
    assignmentsSubject.subscribe(assignmentsTestSubscriber::onNext);
    final TestSubscriber<RetriesExceeded> dispatchRetryExceededTestSubscriber =
        TestSubscriber.create();
    retriesExceededSubject.subscribe(dispatchRetryExceededTestSubscriber::onNext);

    bookings.forEach(bookingsSubject::onNext);

    assignmentsTestSubscriber.assertValueCount(bookingsCount);
    dispatchRetryExceededTestSubscriber.assertValueCount(0);
  }

  @Test
  void testReDispatchForEmptySupply() {
    final int bookingsCount = 10;
    final List<Booking> bookings = generateBookings(bookingsCount);
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation =
        generateCaptainsPerBooking(bookings, ZERO);
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    final ReDispatcher reDispatcher =
        new ReDispatcher(bookingsSubject::onNext, retriesCount, retriesExceededSubject::onNext);
    final Supply supply = new Supply(suppliedCaptainsSubject::onNext, captainsByLocation);
    final Filter filter =
        new Filter(filteredCaptainsSubject::onNext, NO_CAPTAINS_FILTER, reDispatchSubject::onNext);
    final Sort sort = new Sort(sortedCaptainsSubject::onNext);
    final Offers offers = new Offers(offersSubject::onNext);
    final CaptainSimulator captainSimulator =
        new CaptainSimulator(captainResponseSubject::onNext, ALWAYS_ACCEPT_OFFERS);
    final Assignments assignments =
        new Assignments(assignmentsSubject::onNext, reDispatchSubject::onNext);
    wire(reDispatcher, supply, filter, sort, offers, captainSimulator, assignments);
    final TestSubscriber<Assignment> assignmentsTestSubscriber = TestSubscriber.create();
    assignmentsSubject.subscribe(assignmentsTestSubscriber::onNext);
    final TestSubscriber<RetriesExceeded> dispatchRetryExceededTestSubscriber =
        TestSubscriber.create();
    retriesExceededSubject.subscribe(dispatchRetryExceededTestSubscriber::onNext);

    bookings.forEach(bookingsSubject::onNext);

    dispatchRetryExceededTestSubscriber.assertValueCount(bookingsCount);
    assignmentsTestSubscriber.assertValueCount(0);
  }

  @Test
  void testReDispatchForEmptyCaptainsAfterFilter() {
    final int bookingsCount = 10;
    final List<Booking> bookings = generateBookings(bookingsCount);
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation =
        generateCaptainsPerBooking(bookings, AT_LEAST_ONE);
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    final ReDispatcher reDispatcher =
        new ReDispatcher(bookingsSubject::onNext, retriesCount, retriesExceededSubject::onNext);
    final Supply supply = new Supply(suppliedCaptainsSubject::onNext, captainsByLocation);
    final Filter filter =
        new Filter(filteredCaptainsSubject::onNext, ALL_CAPTAINS_FILTER, reDispatchSubject::onNext);
    final Sort sort = new Sort(sortedCaptainsSubject::onNext);
    final Offers offers = new Offers(offersSubject::onNext);
    final CaptainSimulator captainSimulator =
        new CaptainSimulator(captainResponseSubject::onNext, ALWAYS_ACCEPT_OFFERS);
    final Assignments assignments =
        new Assignments(assignmentsSubject::onNext, reDispatchSubject::onNext);
    wire(reDispatcher, supply, filter, sort, offers, captainSimulator, assignments);
    final TestSubscriber<Assignment> assignmentsTestSubscriber = TestSubscriber.create();
    assignmentsSubject.subscribe(assignmentsTestSubscriber::onNext);
    final TestSubscriber<RetriesExceeded> dispatchRetryExceededTestSubscriber =
        TestSubscriber.create();
    retriesExceededSubject.subscribe(dispatchRetryExceededTestSubscriber::onNext);

    bookings.forEach(bookingsSubject::onNext);

    dispatchRetryExceededTestSubscriber.assertValueCount(bookingsCount);
    assignmentsTestSubscriber.assertValueCount(0);
  }

  @Test
  void testReDispatchForCaptainDecliningOffers() {
    final int bookingsCount = 10;
    final List<Booking> bookings = generateBookings(bookingsCount);
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation =
        generateCaptainsPerBooking(bookings, AT_LEAST_ONE);
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    final ReDispatcher reDispatcher =
        new ReDispatcher(bookingsSubject::onNext, retriesCount, retriesExceededSubject::onNext);
    final Supply supply = new Supply(suppliedCaptainsSubject::onNext, captainsByLocation);
    final Filter filter =
        new Filter(filteredCaptainsSubject::onNext, NO_CAPTAINS_FILTER, reDispatchSubject::onNext);
    final Sort sort = new Sort(sortedCaptainsSubject::onNext);
    final Offers offers = new Offers(offersSubject::onNext);
    final CaptainSimulator captainSimulator =
        new CaptainSimulator(captainResponseSubject::onNext, ALWAYS_DECLINE_OFFERS);
    final Assignments assignments =
        new Assignments(assignmentsSubject::onNext, reDispatchSubject::onNext);
    wire(reDispatcher, supply, filter, sort, offers, captainSimulator, assignments);
    final TestSubscriber<Assignment> assignmentsTestSubscriber = TestSubscriber.create();
    assignmentsSubject.subscribe(assignmentsTestSubscriber::onNext);
    final TestSubscriber<RetriesExceeded> dispatchRetryExceededTestSubscriber =
        TestSubscriber.create();
    retriesExceededSubject.subscribe(dispatchRetryExceededTestSubscriber::onNext);

    bookings.forEach(bookingsSubject::onNext);

    dispatchRetryExceededTestSubscriber.assertValueCount(bookingsCount);
    assignmentsTestSubscriber.assertValueCount(0);
  }

  @Test
  void testRealisticFlow() {
    final int bookingsCount = 10;
    final List<Booking> bookings = generateBookings(bookingsCount);
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation =
        generateCaptainsPerBooking(bookings, AT_MOST_EIGHT);
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    final ReDispatcher reDispatcher =
        new ReDispatcher(bookingsSubject::onNext, retriesCount, retriesExceededSubject::onNext);
    final Supply supply = new Supply(suppliedCaptainsSubject::onNext, captainsByLocation);
    final Filter filter =
        new Filter(filteredCaptainsSubject::onNext, RANDOM_CAPTAIN, reDispatchSubject::onNext);
    final Sort sort = new Sort(sortedCaptainsSubject::onNext);
    final Offers offers = new Offers(offersSubject::onNext);
    final CaptainSimulator captainSimulator =
        new CaptainSimulator(captainResponseSubject::onNext, random::nextBoolean);
    final Assignments assignments =
        new Assignments(assignmentsSubject::onNext, reDispatchSubject::onNext);
    wire(reDispatcher, supply, filter, sort, offers, captainSimulator, assignments);
    final TestSubscriber<Assignment> assignmentsTestSubscriber = TestSubscriber.create();
    assignmentsSubject.subscribe(assignmentsTestSubscriber::onNext);
    final TestSubscriber<RetriesExceeded> dispatchRetryExceededTestSubscriber =
        TestSubscriber.create();
    retriesExceededSubject.subscribe(dispatchRetryExceededTestSubscriber::onNext);

    bookings.forEach(bookingsSubject::onNext);

    assertEquals(assignmentsTestSubscriber.valueCount() +
        dispatchRetryExceededTestSubscriber.valueCount(), bookingsCount);
  }

  private ConcurrentMap<Location, Set<Captain>> generateCaptainsPerBooking(
      final List<Booking> bookings, final Supplier<Integer> captainsCountSupplier) {
    return bookings.stream().collect(toConcurrentMap(booking -> booking.pickupLocation,
        booking -> randomCaptains(captainsCountSupplier)));
  }

  private List<Booking> generateBookings(final int bookingsCount) {
    return Stream.generate(this::randomBooking)
        .limit(bookingsCount).collect(toList());
  }

  private void wire(final ReDispatcher reDispatcher, final Supply supply, final Filter filter,
      final Sort sort, final Offers offers, final CaptainSimulator captainSimulator,
      final Assignments assignments) {
    bookingsSubject.subscribe(supply::accept);
    suppliedCaptainsSubject.subscribe(filter::accept);
    filteredCaptainsSubject.subscribe(sort::accept);
    sortedCaptainsSubject.subscribe(offers::accept);
    offersSubject.subscribe(captainSimulator::accept);
    captainResponseSubject.subscribe(assignments::accept);
    reDispatchSubject.subscribe(reDispatcher::accept);
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

  private Set<Captain> randomCaptains(final Supplier<Integer> captainsCountSupplier) {
    return Stream.generate(this::randomCaptain)
        .limit(captainsCountSupplier.get())
        .collect(toSet());
  }

  private Captain randomCaptain() {
    return new Captain(CaptainId.next());
  }
}
