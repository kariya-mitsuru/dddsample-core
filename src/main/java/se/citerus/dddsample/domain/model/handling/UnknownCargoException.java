package se.citerus.dddsample.domain.model.handling;

import lombok.RequiredArgsConstructor;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

/**
 * Thrown when trying to register an event with an unknown tracking id.
 */
@RequiredArgsConstructor
public final class UnknownCargoException extends CannotCreateHandlingEventException {

  private final TrackingId trackingId;

  /**
   * {@inheritDoc}
   */            
  @Override
  public String getMessage() {
    return "No cargo with tracking id " + trackingId.idString() + " exists in the system";
  }
}
