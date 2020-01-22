package se.citerus.dddsample.domain.model.voyage;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.time.LocalDateTime;


/**
 * A carrier movement is a vessel voyage from one location to another.
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor
public final class CarrierMovement implements ValueObject<CarrierMovement> {

  @NonNull @Getter
  private Location departureLocation;
  @NonNull @Getter
  private Location arrivalLocation;
  @NonNull @Getter
  private LocalDateTime departureTime;
  @NonNull @Getter
  private LocalDateTime arrivalTime;

  // Null object pattern 
  public static final CarrierMovement NONE = new CarrierMovement(
    Location.UNKNOWN, Location.UNKNOWN,
    LocalDateTime.MIN, LocalDateTime.MIN
  );

  // Auto-generated surrogate key
  @EqualsAndHashCode.Exclude
  private Long id;
}
