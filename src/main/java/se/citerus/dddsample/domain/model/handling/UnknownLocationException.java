package se.citerus.dddsample.domain.model.handling;

import lombok.RequiredArgsConstructor;
import se.citerus.dddsample.domain.model.location.UnLocode;

@RequiredArgsConstructor
public class UnknownLocationException extends CannotCreateHandlingEventException {

  private final UnLocode unlocode;

  @Override
  public String getMessage() {
    return "No location with UN locode " + unlocode.idString() + " exists in the system";
  }
}
