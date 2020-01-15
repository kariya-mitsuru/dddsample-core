package se.citerus.dddsample.domain.model.voyage;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A Voyage.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class Voyage implements Entity<Voyage> {

  @NonNull @Getter @EqualsAndHashCode.Include
  private VoyageNumber voyageNumber;
  @NonNull @Getter
  private Schedule schedule;

  // Null object pattern
  public static final Voyage NONE = new Voyage(
    new VoyageNumber(""), Schedule.EMPTY
  );

  @Override
  public String toString() {
    return "Voyage " + voyageNumber;
  }

  // Needed by Hibernate
  private Long id;

  /**
   * Builder pattern is used for incremental construction
   * of a Voyage aggregate. This serves as an aggregate factory. 
   */
  public static final class Builder {

    private final List<CarrierMovement> carrierMovements = new ArrayList<CarrierMovement>();
    private final VoyageNumber voyageNumber;
    private Location departureLocation;

    public Builder(@NonNull final VoyageNumber voyageNumber, @NonNull final Location departureLocation) {
      this.voyageNumber = voyageNumber;
      this.departureLocation = departureLocation;
    }

    public Builder addMovement(Location arrivalLocation, Date departureTime, Date arrivalTime) {
      carrierMovements.add(new CarrierMovement(departureLocation, arrivalLocation, departureTime, arrivalTime));
      // Next departure location is the same as this arrival location
      this.departureLocation = arrivalLocation;
      return this;
    }

    public Voyage build() {
      return new Voyage(voyageNumber, new Schedule(carrierMovements));
    }

  }

}
