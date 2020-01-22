package se.citerus.dddsample.domain.model.cargo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.shared.DomainObjectUtils;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * The actual transportation of the cargo, as opposed to
 * the customer requirement (RouteSpecification) and the plan (Itinerary). 
 *
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Delivery implements ValueObject<Delivery> {

  @Getter
  private TransportStatus transportStatus;
  private Location lastKnownLocation;
  private Voyage currentVoyage;
  @Getter @Accessors(fluent = false)
  private boolean misdirected;
  @Getter
  private LocalDateTime estimatedTimeOfArrival;
  @Getter
  private HandlingActivity nextExpectedActivity;
  @Getter
  private boolean isUnloadedAtDestination;
  @Getter
  private RoutingStatus routingStatus;
  @Getter
  private LocalDateTime calculatedAt;
  private HandlingEvent lastEvent;

  private static final LocalDateTime ETA_UNKOWN = null;
  private static final HandlingActivity NO_ACTIVITY = null;

  /**
   * Creates a new delivery snapshot to reflect changes in routing, i.e.
   * when the route specification or the itinerary has changed
   * but no additional handling of the cargo has been performed.
   *
   * @param routeSpecification route specification
   * @param itinerary itinerary
   * @return An up to date delivery
   */
  Delivery updateOnRouting(@NonNull RouteSpecification routeSpecification, Itinerary itinerary) {
    return new Delivery(this.lastEvent, itinerary, routeSpecification);
  }

  /**
   * Creates a new delivery snapshot based on the complete handling history of a cargo,
   * as well as its route specification and itinerary.
   *
   * @param routeSpecification route specification
   * @param itinerary itinerary
   * @param handlingHistory delivery history
   * @return An up to date delivery.
   */
  static Delivery derivedFrom(@NonNull RouteSpecification routeSpecification, Itinerary itinerary, @NonNull HandlingHistory handlingHistory) {
    final HandlingEvent lastEvent = handlingHistory.mostRecentlyCompletedEvent();

    return new Delivery(lastEvent, itinerary, routeSpecification);
  }

  /**
   * Internal constructor.
   *
   * @param lastEvent last event
   * @param itinerary itinerary
   * @param routeSpecification route specification
   */
  private Delivery(HandlingEvent lastEvent, Itinerary itinerary, RouteSpecification routeSpecification) {
    this.calculatedAt = LocalDateTime.now();
    this.lastEvent = lastEvent;

    this.misdirected = calculateMisdirectionStatus(itinerary);
    this.routingStatus = calculateRoutingStatus(itinerary, routeSpecification);
    this.transportStatus = calculateTransportStatus();
    this.lastKnownLocation = calculateLastKnownLocation();
    this.currentVoyage = calculateCurrentVoyage();
    this.estimatedTimeOfArrival = calculateEta(itinerary);
    this.nextExpectedActivity = calculateNextExpectedActivity(routeSpecification, itinerary);
    this.isUnloadedAtDestination = calculateUnloadedAtDestination(routeSpecification);
  }

  /**
   * @return Last known location of the cargo, or Location.UNKNOWN if the delivery history is empty.
   */
  public Location lastKnownLocation() {
    return DomainObjectUtils.nullSafe(lastKnownLocation, Location.UNKNOWN);
  }

  /**
   * @return Current voyage.
   */
  public Voyage currentVoyage() {
    return DomainObjectUtils.nullSafe(currentVoyage, Voyage.NONE);
  }

  // TODO add currentCarrierMovement (?)


  // --- Internal calculations below ---


  private TransportStatus calculateTransportStatus() {
    if (lastEvent == null) {
      return NOT_RECEIVED;
    }

    switch (lastEvent.type()) {
      case LOAD:
        return ONBOARD_CARRIER;
      case UNLOAD:
      case RECEIVE:
      case CUSTOMS:
        return IN_PORT;
      case CLAIM:
        return CLAIMED;
      default:
        return UNKNOWN;
    }
  }

  private Location calculateLastKnownLocation() {
    if (lastEvent != null) {
      return lastEvent.location();
    } else {
      return null;
    }
  }

  private Voyage calculateCurrentVoyage() {
    if (transportStatus().equals(ONBOARD_CARRIER) && lastEvent != null) {
      return lastEvent.voyage();
    } else {
      return null;
    }
  }

  private boolean calculateMisdirectionStatus(Itinerary itinerary) {
    if (lastEvent == null) {
      return false;
    } else {
      return !itinerary.isExpected(lastEvent);
    }
  }

  private LocalDateTime calculateEta(Itinerary itinerary) {
    if (onTrack()) {
      return itinerary.finalArrivalDate();
    } else {
      return ETA_UNKOWN;
    }
  }

  private HandlingActivity calculateNextExpectedActivity(RouteSpecification routeSpecification, Itinerary itinerary) {
    if (!onTrack()) return NO_ACTIVITY;

    if (lastEvent == null) return new HandlingActivity(HandlingEvent.Type.RECEIVE, routeSpecification.origin());

    switch (lastEvent.type()) {

      case LOAD:
        for (Leg leg : itinerary.legs()) {
          if (leg.loadLocation().sameIdentityAs(lastEvent.location())) {
            return new HandlingActivity(HandlingEvent.Type.UNLOAD, leg.unloadLocation(), leg.voyage());
          }
        }

        return NO_ACTIVITY;

      case UNLOAD:
        for (Iterator<Leg> it = itinerary.legs().iterator(); it.hasNext();) {
          final Leg leg = it.next();
          if (leg.unloadLocation().sameIdentityAs(lastEvent.location())) {
            if (it.hasNext()) {
              final Leg nextLeg = it.next();
              return new HandlingActivity(HandlingEvent.Type.LOAD, nextLeg.loadLocation(), nextLeg.voyage());
            } else {
              return new HandlingActivity(HandlingEvent.Type.CLAIM, leg.unloadLocation());
            }
          }
        }

        return NO_ACTIVITY;

      case RECEIVE:
        final Leg firstLeg = itinerary.legs().iterator().next();
        return new HandlingActivity(HandlingEvent.Type.LOAD, firstLeg.loadLocation(), firstLeg.voyage());

      case CLAIM:
      default:
        return NO_ACTIVITY;
    }
  }

  private RoutingStatus calculateRoutingStatus(Itinerary itinerary, RouteSpecification routeSpecification) {
    if (itinerary == null) {
      return NOT_ROUTED;
    } else {
      if (routeSpecification.isSatisfiedBy(itinerary)) {
        return ROUTED;
      } else {
        return MISROUTED;
      }
    }
  }

  private boolean calculateUnloadedAtDestination(RouteSpecification routeSpecification) {
    return lastEvent != null &&
      HandlingEvent.Type.UNLOAD.sameValueAs(lastEvent.type()) &&
      routeSpecification.destination().sameIdentityAs(lastEvent.location());
  }

  private boolean onTrack() {
    return routingStatus.equals(ROUTED) && !misdirected;
  }
}
