package io.github.ilyazinkovich.reactive.dispatch.redispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;

public interface FailedDispatchBookings {

  void add(final Booking booking);
}
