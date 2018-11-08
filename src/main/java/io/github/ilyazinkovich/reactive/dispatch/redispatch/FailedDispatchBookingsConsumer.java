package io.github.ilyazinkovich.reactive.dispatch.redispatch;

import io.github.ilyazinkovich.reactive.dispatch.core.Booking;

public interface FailedDispatchBookingsConsumer {

  void accept(final Booking booking);
}
