package se.citerus.dddsample.infrastructure.messaging.jms;

import lombok.extern.apachecommons.CommonsLog;

import javax.jms.Message;
import javax.jms.MessageListener;

@CommonsLog
public class SimpleLoggingConsumer implements MessageListener {

  @Override
  public void onMessage(Message message) {
    log.debug("Received JMS message: " + message);
  }

}
