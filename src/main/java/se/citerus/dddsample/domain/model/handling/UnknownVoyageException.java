package se.citerus.dddsample.domain.model.handling;

import lombok.RequiredArgsConstructor;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

/**
 * Thrown when trying to register an event with an unknown carrier movement id.
 */
@RequiredArgsConstructor
public class UnknownVoyageException extends CannotCreateHandlingEventException {

  private final VoyageNumber voyageNumber;

  @Override
  public String getMessage() {
    return "No voyage with number " + voyageNumber.idString() + " exists in the system";
  }
}
