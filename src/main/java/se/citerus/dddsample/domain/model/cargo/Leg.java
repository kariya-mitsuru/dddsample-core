package se.citerus.dddsample.domain.model.cargo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.time.LocalDateTime;

/**
 * An itinerary consists of one or more legs.
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class Leg implements ValueObject<Leg> {

  @NonNull @Getter
  private Voyage voyage;
  @NonNull @Getter
  private Location loadLocation;
  @NonNull @Getter
  private Location unloadLocation;
  @NonNull @Getter
  private LocalDateTime loadTime;
  @NonNull @Getter
  private LocalDateTime unloadTime;

  // Auto-generated surrogate key
  @EqualsAndHashCode.Exclude
  private Long id;

}
