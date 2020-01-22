package se.citerus.dddsample.interfaces.booking.facade.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for registering and routing a cargo.
 */
@RequiredArgsConstructor
public final class CargoRoutingDTO implements Serializable {

  @Getter
  private final String trackingId;
  @Getter
  private final String origin;
  @Getter
  private final String finalDestination;
  @Getter
  private final LocalDateTime arrivalDeadline;
  @Getter
  private final boolean misrouted;
  private final List<LegDTO> legs = new ArrayList<LegDTO>();

  public void addLeg(String voyageNumber, String from, String to, LocalDateTime loadTime, LocalDateTime unloadTime) {
    legs.add(new LegDTO(voyageNumber, from, to, loadTime, unloadTime));
  }

  /**
   * @return An unmodifiable list DTOs.
   */
  public List<LegDTO> getLegs() {
    return Collections.unmodifiableList(legs);
  }

  public boolean isRouted() {
    return !legs.isEmpty();
  }
}
