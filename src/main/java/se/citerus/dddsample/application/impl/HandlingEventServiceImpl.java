package se.citerus.dddsample.application.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.util.Date;

@RequiredArgsConstructor
@CommonsLog
public class HandlingEventServiceImpl implements HandlingEventService {

  private final HandlingEventRepository handlingEventRepository;
  private final ApplicationEvents applicationEvents;
  private final HandlingEventFactory handlingEventFactory;

  @Override
  @Transactional(rollbackFor = CannotCreateHandlingEventException.class)
  public void registerHandlingEvent(final Date completionTime,
                                    final TrackingId trackingId,
                                    final VoyageNumber voyageNumber,
                                    final UnLocode unLocode,
                                    final HandlingEvent.Type type) throws CannotCreateHandlingEventException {
    final Date registrationTime = new Date();
    /* Using a factory to create a HandlingEvent (aggregate). This is where
       it is determined whether the incoming data, the attempt, actually is capable
       of representing a real handling event. */
    final HandlingEvent event = handlingEventFactory.createHandlingEvent(
      registrationTime, completionTime, trackingId, voyageNumber, unLocode, type
    );

    /* Store the new handling event, which updates the persistent
       state of the handling event aggregate (but not the cargo aggregate -
       that happens asynchronously!)
     */
    handlingEventRepository.store(event);

    /* Publish an event stating that a cargo has been handled. */
    applicationEvents.cargoWasHandled(event);

    log.info("Registered handling event");
  }

}
