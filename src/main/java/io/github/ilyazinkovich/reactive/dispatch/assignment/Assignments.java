package io.github.ilyazinkovich.reactive.dispatch.assignment;

import static io.reactivex.BackpressureStrategy.BUFFER;

import io.github.ilyazinkovich.reactive.dispatch.captain.CaptainResponse;
import io.github.ilyazinkovich.reactive.dispatch.offer.ReDispatch;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class Assignments {

  public final Flowable<Assignment> assignmentsStream;

  public Assignments(final Flowable<CaptainResponse> captainResponseStream,
      final PublishSubject<ReDispatch> reDispatchesSubject) {
    final PublishSubject<Assignment> assignmentsSubject = PublishSubject.create();
    captainResponseStream.subscribe(captainResponse -> {
      if (captainResponse.accepted) {
        final Assignment assignment =
            new Assignment(captainResponse.booking, captainResponse.captainId);
        assignmentsSubject.onNext(assignment);
      } else {
        reDispatchesSubject.onNext(new ReDispatch(captainResponse.booking));
      }
    });
    this.assignmentsStream = assignmentsSubject.toFlowable(BUFFER);
  }
}
