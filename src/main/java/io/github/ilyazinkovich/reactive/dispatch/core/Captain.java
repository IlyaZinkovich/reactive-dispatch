package io.github.ilyazinkovich.reactive.dispatch.core;

import java.util.Comparator;
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
}
