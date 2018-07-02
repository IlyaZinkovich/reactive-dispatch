package io.github.ilyazinkovich.reactive.dispatch.assignment;

import io.github.ilyazinkovich.reactive.dispatch.captain.CaptainResponse;
import io.github.ilyazinkovich.reactive.dispatch.core.ReDispatch;
import java.util.function.Consumer;

public class Assignments {

  private final Consumer<Assignment> assignmentConsumer;
  private final Consumer<ReDispatch> reDispatchConsumer;

  public Assignments(final Consumer<Assignment> assignmentsConsumer,
      final Consumer<ReDispatch> reDispatchConsumer) {
    this.assignmentConsumer = assignmentsConsumer;
    this.reDispatchConsumer = reDispatchConsumer;
  }

  public void accept(final CaptainResponse captainResponse) {
    if (captainResponse.accepted) {
      final Assignment assignment =
          new Assignment(captainResponse.booking, captainResponse.captainId);
      assignmentConsumer.accept(assignment);
    } else {
      reDispatchConsumer.accept(new ReDispatch(captainResponse.booking));
    }
  }
}
