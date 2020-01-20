package se.citerus.dddsample.infrastructure.messaging.jms;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * JMS based implementation.
 */
@CommonsLog
@RequiredArgsConstructor
public final class JmsApplicationEventsImpl implements ApplicationEvents {

  private final JmsOperations jmsOperations;
  @Qualifier("cargoHandledQueue")
  private final Destination cargoHandledQueue;
  @Qualifier("misdirectedCargoQueue")
  private final Destination misdirectedCargoQueue;
  @Qualifier("deliveredCargoQueue")
  private final Destination deliveredCargoQueue;
  @Qualifier("rejectedRegistrationAttemptsQueue")
  private final Destination rejectedRegistrationAttemptsQueue;
  @Qualifier("handlingEventQueue")
  private final Destination handlingEventQueue;

  @Override
  public void cargoWasHandled(final HandlingEvent event) {
    final Cargo cargo = event.cargo();
    log.info("Cargo was handled " + cargo);
    jmsOperations.send(cargoHandledQueue, new MessageCreator() {
      public Message createMessage(final Session session) throws JMSException {
        return session.createTextMessage(cargo.trackingId().idString());
      }
    });
  }

  @Override
  public void cargoWasMisdirected(final Cargo cargo) {
    log.info("Cargo was misdirected " + cargo);
    jmsOperations.send(misdirectedCargoQueue, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(cargo.trackingId().idString());
      }
    });
  }

  @Override
  public void cargoHasArrived(final Cargo cargo) {
    log.info("Cargo has arrived " + cargo);
    jmsOperations.send(deliveredCargoQueue, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(cargo.trackingId().idString());
      }
    });
  }

  @Override
  public void receivedHandlingEventRegistrationAttempt(final HandlingEventRegistrationAttempt attempt) {
    log.info("Received handling event registration attempt " + attempt);
    jmsOperations.send(handlingEventQueue, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createObjectMessage(attempt);
      }
    });
  }
}
