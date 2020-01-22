package se.citerus.dddsample.domain.model.handling;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.shared.DomainEvent;
import se.citerus.dddsample.domain.shared.DomainObjectUtils;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.time.LocalDateTime;

/**
 * A HandlingEvent is used to register the event when, for instance,
 * a cargo is unloaded from a carrier at some location at a given time.
 * <p/>
 * The HandlingEvent's are sent from different Incident Logging Applications
 * some time after the event occurred and contain information about the
 * {@link se.citerus.dddsample.domain.model.cargo.TrackingId}, {@link se.citerus.dddsample.domain.model.location.Location}, timestamp of the completion of the event,
 * and possibly, if applicable a {@link se.citerus.dddsample.domain.model.voyage.Voyage}.
 * <p/>
 * This class is the only member, and consequently the root, of the HandlingEvent aggregate. 
 * <p/>
 * HandlingEvent's could contain information about a {@link Voyage} and if so,
 * the event type must be either {@link Type#LOAD} or {@link Type#UNLOAD}.
 * <p/>
 * All other events must be of {@link Type#RECEIVE}, {@link Type#CLAIM} or {@link Type#CUSTOMS}.
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class HandlingEvent implements DomainEvent<HandlingEvent> {

  @Getter
  private Type type;
  private Voyage voyage;
  @Getter
  private Location location;
  @Getter
  private LocalDateTime completionTime;
  @Getter @EqualsAndHashCode.Exclude
  private LocalDateTime registrationTime;
  @Getter
  private Cargo cargo;

  /**
   * Handling event type. Either requires or prohibits a carrier movement
   * association, it's never optional.
   */
  @RequiredArgsConstructor
  public enum Type implements ValueObject<Type> {
    LOAD(true),
    UNLOAD(true),
    RECEIVE(false),
    CLAIM(false),
    CUSTOMS(false);

    @Getter
    private final boolean requiresVoyage;

    /**
     * @return True if a voyage association is prohibited for this event type.
     */
    public boolean prohibitsVoyage() {
      return !requiresVoyage();
    }
  }

  /**
   * @param cargo            cargo
   * @param completionTime   completion time, the reported time that the event actually happened (e.g. the receive took place).
   * @param registrationTime registration time, the time the message is received
   * @param type             type of event
   * @param location         where the event took place
   * @param voyage           the voyage
   */
  public HandlingEvent(@NonNull final Cargo cargo,
                       @NonNull final LocalDateTime completionTime,
                       @NonNull final LocalDateTime registrationTime,
                       @NonNull final Type type,
                       @NonNull final Location location,
                       @NonNull final Voyage voyage) {
    if (type.prohibitsVoyage()) {
      throw new IllegalArgumentException("Voyage is not allowed with event type " + type);
    }

    this.voyage = voyage;
    this.completionTime = completionTime;
    this.registrationTime = registrationTime;
    this.type = type;
    this.location = location;
    this.cargo = cargo;
  }

  /**
   * @param cargo            cargo
   * @param completionTime   completion time, the reported time that the event actually happened (e.g. the receive took place).
   * @param registrationTime registration time, the time the message is received
   * @param type             type of event
   * @param location         where the event took place
   */
  public HandlingEvent(@NonNull final Cargo cargo,
                       @NonNull final LocalDateTime completionTime,
                       @NonNull final LocalDateTime registrationTime,
                       @NonNull final Type type,
                       @NonNull final Location location) {
    if (type.requiresVoyage()) {
      throw new IllegalArgumentException("Voyage is required for event type " + type);
    }

    this.completionTime = completionTime;
    this.registrationTime = registrationTime;
    this.type = type;
    this.location = location;
    this.cargo = cargo;
    this.voyage = null;
  }

  public Voyage voyage() {
    return DomainObjectUtils.nullSafe(this.voyage, Voyage.NONE);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder("\n--- Handling event ---\n").
      append("Cargo: ").append(cargo.trackingId()).append("\n").
      append("Type: ").append(type).append("\n").
      append("Location: ").append(location.name()).append("\n").
      append("Completed on: ").append(completionTime).append("\n").
      append("Registered on: ").append(registrationTime).append("\n");
    
    if (voyage != null) {
      builder.append("Voyage: ").append(voyage.voyageNumber()).append("\n");
    }

    return builder.toString();
  }

  // Auto-generated surrogate key
  @EqualsAndHashCode.Exclude
  private Long id;

}
