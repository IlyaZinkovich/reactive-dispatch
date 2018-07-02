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
import io.github.ilyazinkovich.reactive.dispatch.redispatch.DispatchRetryExceeded;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.ReDispatcher;
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
  private static final Supplier<Boolean> ALWAYS_ACCEPT_OFFERS = () -> true;
  private static final Supplier<Integer> AT_LEAST_ONE = () -> random.nextInt(8) + 1;
  private static final Predicate<Captain> NO_CAPTAIN_FILTER = captain -> true;
  private final PublishSubject<Booking> bookingsSubject = PublishSubject.create();
  private final PublishSubject<ReDispatch> reDispatchesSubject = PublishSubject.create();
  private final PublishSubject<SuppliedCaptains> suppliedCaptainsSubject = PublishSubject.create();
  private final PublishSubject<FilteredCaptains> filteredCaptainsSubject = PublishSubject.create();
  private final PublishSubject<SortedCaptains> sortedCaptainsSubject = PublishSubject.create();
  private final PublishSubject<Offer> offersSubject = PublishSubject.create();
  private final PublishSubject<CaptainResponse> captainResponseSubject = PublishSubject.create();
  private final PublishSubject<Assignment> assignmentsSubject = PublishSubject.create();
  private final PublishSubject<DispatchRetryExceeded> dispatchRetryExceededSubject =
      PublishSubject.create();

  @Test
  void test() {
    final int bookingsCount = 10;
    final List<Booking> bookings = Stream.generate(this::randomBooking)
        .limit(bookingsCount).collect(toList());
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation = bookings.stream()
        .collect(toConcurrentMap(booking -> booking.pickupLocation, booking -> randomCaptains(
            AT_LEAST_ONE)));
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    final ReDispatcher reDispatcher = new ReDispatcher(bookingsSubject, retriesCount,
        dispatchRetryExceededSubject);
    final Supply supply = new Supply(suppliedCaptainsSubject, captainsByLocation);
    final Filter filter = new Filter(filteredCaptainsSubject, NO_CAPTAIN_FILTER);
    final Sort sort = new Sort(sortedCaptainsSubject);
    final Offers offers = new Offers(offersSubject, reDispatchesSubject);
    final CaptainSimulator captainSimulator =
        new CaptainSimulator(captainResponseSubject, ALWAYS_ACCEPT_OFFERS);
    final Assignments assignments = new Assignments(assignmentsSubject, reDispatchesSubject);
    wire(reDispatcher, supply, filter, sort, offers, captainSimulator, assignments);
    final TestSubscriber<Assignment> assignmentsTestSubscriber = TestSubscriber.create();
    assignments.subscribeAssignments(assignmentsTestSubscriber::onNext);
    final TestSubscriber<DispatchRetryExceeded> dispatchRetryExceededTestSubscriber =
        TestSubscriber.create();
    reDispatcher.subscribe(dispatchRetryExceededTestSubscriber::onNext);

    bookings.forEach(bookingsSubject::onNext);

    assignmentsTestSubscriber.assertValueCount(10);
    dispatchRetryExceededTestSubscriber.assertValueCount(0);
  }

  private void wire(final ReDispatcher reDispatcher, final Supply supply, final Filter filter,
      final Sort sort, final Offers offers, final CaptainSimulator captainSimulator,
      final Assignments assignments) {
    bookingsSubject.subscribe(supply);
    supply.subscribe(filter);
    filter.subscribe(sort);
    sort.subscribe(offers);
    offers.subscribeOffers(captainSimulator);
    offers.subscribeReDispatches(reDispatcher);
    captainSimulator.subscribe(assignments);
    assignments.subscribeReDispatches(reDispatcher);
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
