package io.github.ilyazinkovich.reactive.dispatch;

import java.util.Objects;
import java.util.UUID;

public class BookingId {

  final String uid;

  public BookingId(final String uid) {
    this.uid = uid;
  }

  public static BookingId next() {
    return new BookingId(UUID.randomUUID().toString());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final BookingId bookingId = (BookingId) o;
    return Objects.equals(uid, bookingId.uid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uid);
  }
}
