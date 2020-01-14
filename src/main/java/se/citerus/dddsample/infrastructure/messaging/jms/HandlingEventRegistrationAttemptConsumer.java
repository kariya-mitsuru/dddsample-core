package se.citerus.dddsample.infrastructure.messaging.jms;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * Consumes handling event registration attempt messages and delegates to
 * proper registration.
 * 
 */
@RequiredArgsConstructor
@CommonsLog
public class HandlingEventRegistrationAttemptConsumer implements MessageListener {

  private final HandlingEventService handlingEventService;

  @Override
  public void onMessage(final Message message) {
    try {
      final ObjectMessage om = (ObjectMessage) message;
      HandlingEventRegistrationAttempt attempt = (HandlingEventRegistrationAttempt) om.getObject();
      handlingEventService.registerHandlingEvent(
        attempt.getCompletionTime(),
        attempt.getTrackingId(),
        attempt.getVoyageNumber(),
        attempt.getUnLocode(),
        attempt.getType()
      );
    } catch (Exception e) {
      log.error(e, e);
    }
  }
}
