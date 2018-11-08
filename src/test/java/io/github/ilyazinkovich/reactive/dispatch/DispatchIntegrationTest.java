package io.github.ilyazinkovich.reactive.dispatch;

import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;
import io.github.ilyazinkovich.reactive.dispatch.core.BookingId;
import io.github.ilyazinkovich.reactive.dispatch.core.Captain;
import io.github.ilyazinkovich.reactive.dispatch.core.CaptainId;
import io.github.ilyazinkovich.reactive.dispatch.core.Location;
import io.github.ilyazinkovich.reactive.dispatch.filter.Filter;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offer;
import io.github.ilyazinkovich.reactive.dispatch.offer.Offers;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.FailedDispatchBookings;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.ReDispatcher;
import io.github.ilyazinkovich.reactive.dispatch.sort.Sort;
import io.github.ilyazinkovich.reactive.dispatch.supply.Supply;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

class DispatchIntegrationTest {

  private static final Random random = new Random();
  private static final Predicate<Captain> RANDOM_CAPTAIN_FILTER = captain -> random.nextBoolean();
  private static final Supplier<Integer> AT_LEAST_ONE = () -> random.nextInt(8) + 1;
  private static final Supplier<Integer> AT_MOST_EIGHT = () -> random.nextInt(8);
  private static final Supplier<Integer> ZERO = () -> 0;
  private static final Predicate<Captain> NO_CAPTAINS_FILTER = captain -> true;
  private static final Predicate<Captain> ALL_CAPTAINS_FILTER = captain -> false;
  private static final Duration NO_RETRY_DELAY = Duration.ZERO;
  private static final Scheduler RETRY_SCHEDULER = Schedulers.single();
  private static final int NO_RETRIES = 0;
  private static final int SINGLE_RETRY = 1;
  private static final Duration SMALL_RETRY_DELAY = Duration.ofMillis(10);

  @Test
  void testOptimisticFlow() {
    final int bookingsCount = 10;
    final List<Booking> bookings = generateBookings(bookingsCount);
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation =
        generateCaptainsPerBooking(bookings, AT_LEAST_ONE);
    final Supply supply = new Supply(captainsByLocation);
    final Filter filter = new Filter(NO_CAPTAINS_FILTER);
    final Sort sort = new Sort();
    final Offers offers = new Offers();
    final ReDispatcher reDispatcher = noRedispatch();
    final Dispatch dispatch = new Dispatch(supply, filter, sort, offers, reDispatcher);
    final Flux<Offer> offerStream = Flux.fromIterable(bookings).flatMap(dispatch::dispatch);
    offerStream.toStream().forEach(offer ->
        System.out.println(offer.booking.id.uid + " " + offer.captainId.uid));
    assertEquals(Long.valueOf(bookingsCount), offerStream.count().block());
  }

  @Test
  void testEmptySupply() {
    final int bookingsCount = 10;
    final List<Booking> bookings = generateBookings(bookingsCount);
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation =
        generateCaptainsPerBooking(bookings, ZERO);
    final Supply supply = new Supply(captainsByLocation);
    final Filter filter = new Filter(NO_CAPTAINS_FILTER);
    final Sort sort = new Sort();
    final Offers offers = new Offers();
    final Queue<Booking> reDispatchedBookings = new ArrayDeque<>();
    final ReDispatcher reDispatcher = noRedispatch(reDispatchedBookings::offer);
    final Dispatch dispatch = new Dispatch(supply, filter, sort, offers, reDispatcher);
    final Flux<Offer> offerStream = Flux.fromIterable(bookings).flatMap(dispatch::dispatch);
    assertEquals(Long.valueOf(0), offerStream.count().block());
    assertEquals(bookingsCount, reDispatchedBookings.size());
  }

  @Test
  void testReDispatchForEmptyCaptainsAfterFilter() {
    final int bookingsCount = 10;
    final List<Booking> bookings = generateBookings(bookingsCount);
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation =
        generateCaptainsPerBooking(bookings, AT_LEAST_ONE);
    final Supply supply = new Supply(captainsByLocation);
    final Filter filter = new Filter(ALL_CAPTAINS_FILTER);
    final Sort sort = new Sort();
    final Offers offers = new Offers();
    final Queue<Booking> reDispatchedBookings = new ArrayDeque<>();
    final ReDispatcher reDispatcher = noRedispatch(reDispatchedBookings::offer);
    final Dispatch dispatch = new Dispatch(supply, filter, sort, offers, reDispatcher);
    final Flux<Offer> offerStream = Flux.fromIterable(bookings).flatMap(dispatch::dispatch);
    assertEquals(Long.valueOf(0), offerStream.count().block());
    assertEquals(bookingsCount, reDispatchedBookings.size());
  }

  @Test
  void testRealisticFlow() {
    final int bookingsCount = 10;
    final List<Booking> bookings = generateBookings(bookingsCount);
    final ConcurrentMap<Location, Set<Captain>> captainsByLocation =
        generateCaptainsPerBooking(bookings, AT_MOST_EIGHT);
    final Supply supply = new Supply(captainsByLocation);
    final Filter filter = new Filter(RANDOM_CAPTAIN_FILTER);
    final Sort sort = new Sort();
    final Offers offers = new Offers();
    final Queue<Booking> reDispatchedBookings = new ArrayDeque<>();
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    final ReDispatcher reDispatcher = new ReDispatcher(SINGLE_RETRY, SMALL_RETRY_DELAY,
        RETRY_SCHEDULER, retriesCount, reDispatchedBookings::offer);
    final Dispatch dispatch = new Dispatch(supply, filter, sort, offers, reDispatcher);
    final Flux<Offer> offerStream = Flux.fromIterable(bookings).flatMap(dispatch::dispatch);
    assertEquals(bookingsCount, offerStream.count().block() + reDispatchedBookings.size());
  }

  private ConcurrentMap<Location, Set<Captain>> generateCaptainsPerBooking(
      final List<Booking> bookings, final Supplier<Integer> captainsCountSupplier) {
    return bookings.stream().collect(toConcurrentMap(booking -> booking.pickupLocation,
        booking -> randomCaptains(captainsCountSupplier)));
  }

  private ReDispatcher noRedispatch() {
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    return new ReDispatcher(NO_RETRIES, NO_RETRY_DELAY, RETRY_SCHEDULER, retriesCount,
        System.out::println);
  }

  private ReDispatcher noRedispatch(final FailedDispatchBookings consumer) {
    final Map<BookingId, AtomicInteger> retriesCount = new ConcurrentHashMap<>();
    return new ReDispatcher(NO_RETRIES, NO_RETRY_DELAY, RETRY_SCHEDULER, retriesCount, consumer);
  }

  private List<Booking> generateBookings(final int bookingsCount) {
    return Stream.generate(this::randomBooking)
        .limit(bookingsCount).collect(toList());
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
