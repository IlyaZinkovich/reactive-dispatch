package io.github.ilyazinkovich.reactive.dispatch.assignment;

import io.github.ilyazinkovich.reactive.dispatch.captain.CaptainResponse;
import java.util.function.Consumer;

public class Assignments {

  private final Consumer<Assignment> assignmentConsumer;

  public Assignments(final Consumer<Assignment> assignmentsConsumer) {
    this.assignmentConsumer = assignmentsConsumer;
  }

  public void accept(final CaptainResponse captainResponse) {
    if (captainResponse.accepted) {
      final Assignment assignment =
          new Assignment(captainResponse.booking, captainResponse.captainId);
      assignmentConsumer.accept(assignment);
    }
  }
}
