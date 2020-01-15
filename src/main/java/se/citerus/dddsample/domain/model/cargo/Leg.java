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

import java.util.Date;

/**
 * An itinerary consists of one or more legs.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Leg implements ValueObject<Leg> {

  @NonNull @Getter
  private Voyage voyage;
  @NonNull @Getter
  private Location loadLocation;
  @NonNull @Getter
  private Location unloadLocation;
  @NonNull
  private Date loadTime;
  @NonNull
  private Date unloadTime;

  public Date loadTime() {
    return new Date(loadTime.getTime());
  }

  public Date unloadTime() {
    return new Date(unloadTime.getTime());
  }

  // Auto-generated surrogate key
  @EqualsAndHashCode.Exclude
  private Long id;

}
