package io.github.ilyazinkovich.reactive.dispatch.core;

import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

public class Captain {

  public final CaptainId id;

  public Captain(final CaptainId id) {
    this.id = id;
  }

  public static Comparator<Captain> comparator() {
    final Random random = new Random();
    return (left, right) -> random.nextInt(2) - 1;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Captain captain = (Captain) o;
    return Objects.equals(id, captain.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
