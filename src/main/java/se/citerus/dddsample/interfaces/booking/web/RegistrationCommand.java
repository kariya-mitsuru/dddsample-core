package se.citerus.dddsample.interfaces.booking.web;

import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter @Setter
public final class RegistrationCommand {

  private String originUnlocode;
  private String destinationUnlocode;
  private String arrivalDeadline;
}
