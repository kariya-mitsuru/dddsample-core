package se.citerus.dddsample.domain.model.cargo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.Specification;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.Date;

/**
 * Route specification. Describes where a cargo origin and destination is,
 * and the arrival deadline.
 * 
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RouteSpecification implements Specification<Itinerary>, ValueObject<RouteSpecification> {

  @Getter
  private Location origin;
  @Getter
  private Location destination;
  private Date arrivalDeadline;

  /**
   * @param origin origin location - can't be the same as the destination
   * @param destination destination location - can't be the same as the origin
   * @param arrivalDeadline arrival deadline
   */
  public RouteSpecification(final Location origin, final Location destination, final Date arrivalDeadline) {
    Validate.notNull(origin, "Origin is required");
    Validate.notNull(destination, "Destination is required");
    Validate.notNull(arrivalDeadline, "Arrival deadline is required");
    Validate.isTrue(!origin.sameIdentityAs(destination), "Origin and destination can't be the same: " + origin);

    this.origin = origin;
    this.destination = destination;
    this.arrivalDeadline = (Date) arrivalDeadline.clone();
  }

  /**
   * @return Arrival deadline.
   */
  public Date arrivalDeadline() {
    return new Date(arrivalDeadline.getTime());
  }

  @Override
  public boolean isSatisfiedBy(final Itinerary itinerary) {
    return itinerary != null &&
           origin().sameIdentityAs(itinerary.initialDepartureLocation()) &&
           destination().sameIdentityAs(itinerary.finalArrivalLocation()) &&
           arrivalDeadline().after(itinerary.finalArrivalDate());
  }
}
