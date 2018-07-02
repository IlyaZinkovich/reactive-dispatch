package io.github.ilyazinkovich.reactive.dispatch.assignment;

import io.github.ilyazinkovich.reactive.dispatch.captain.CaptainResponse;
import io.github.ilyazinkovich.reactive.dispatch.redispatch.ReDispatch;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class Assignments implements Consumer<CaptainResponse> {

  private final PublishSubject<Assignment> assignmentsSubject;
  private final PublishSubject<ReDispatch> reDispatchesSubject;

  public Assignments(final PublishSubject<Assignment> assignmentsSubject,
      final PublishSubject<ReDispatch> reDispatchesSubject) {
    this.assignmentsSubject = assignmentsSubject;
    this.reDispatchesSubject = reDispatchesSubject;
  }

  @Override
  public void accept(final CaptainResponse captainResponse) {
    if (captainResponse.accepted) {
      final Assignment assignment =
          new Assignment(captainResponse.booking, captainResponse.captainId);
      assignmentsSubject.onNext(assignment);
    } else {
      reDispatchesSubject.onNext(new ReDispatch(captainResponse.booking));
    }
  }

  public void subscribeAssignments(final Consumer<Assignment> consumer) {
    assignmentsSubject.subscribe(consumer);
  }

  public void subscribeReDispatches(final Consumer<ReDispatch> consumer) {
    reDispatchesSubject.subscribe(consumer);
  }
}
