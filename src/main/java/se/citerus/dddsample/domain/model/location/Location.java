package se.citerus.dddsample.domain.model.location;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import se.citerus.dddsample.domain.shared.Entity;

/**
 * A location is our model is stops on a journey, such as cargo
 * origin or destination, or carrier movement endpoints.
 *
 * It is uniquely identified by a UN Locode.
 *
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Location implements Entity<Location> {

  @NonNull @Getter @EqualsAndHashCode.Include
  private UnLocode unLocode;
  @NonNull @Getter
  private String name;

  /**
   * Special Location object that marks an unknown location.
   */
  public static final Location UNKNOWN = new Location(
    new UnLocode("XXXXX"), "Unknown location"
  );

  @Override
  public String toString() {
    return name + " [" + unLocode + "]";
  }

  private Long id;

}
