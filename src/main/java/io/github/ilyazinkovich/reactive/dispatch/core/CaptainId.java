package io.github.ilyazinkovich.reactive.dispatch.core;

import java.util.Objects;
import java.util.UUID;

public class CaptainId {

  final String uid;

  public CaptainId(final String uid) {
    this.uid = uid;
  }

  public static CaptainId next() {
    return new CaptainId(UUID.randomUUID().toString());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final CaptainId captainId = (CaptainId) o;
    return Objects.equals(uid, captainId.uid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uid);
  }
}
