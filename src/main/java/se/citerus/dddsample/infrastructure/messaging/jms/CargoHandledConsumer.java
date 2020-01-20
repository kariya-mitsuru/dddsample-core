package se.citerus.dddsample.infrastructure.messaging.jms;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import se.citerus.dddsample.application.CargoInspectionService;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Consumes JMS messages and delegates notification of misdirected
 * cargo to the tracking service.
 *
 * This is a programmatic hook into the JMS infrastructure to
 * make cargo inspection message-driven.
 */
@CommonsLog
@RequiredArgsConstructor
public class CargoHandledConsumer implements MessageListener {

  private final CargoInspectionService cargoInspectionService;

  @Override  
  public void onMessage(final Message message) {
    try {
      final TextMessage textMessage = (TextMessage) message;
      final String trackingidString = textMessage.getText();
      
      cargoInspectionService.inspectCargo(new TrackingId(trackingidString));
    } catch (Exception e) {
      log.error(e, e);
    }
  }
}
