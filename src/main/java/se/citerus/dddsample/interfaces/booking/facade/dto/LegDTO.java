package se.citerus.dddsample.interfaces.booking.facade.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * DTO for a leg in an itinerary.
 */
@Getter
@RequiredArgsConstructor
public final class LegDTO implements Serializable {

  private final String voyageNumber;
  private final String from;
  private final String to;
  private final Date loadTime;
  private final Date unloadTime;
}
