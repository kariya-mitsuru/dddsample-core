package se.citerus.dddsample.domain.model.cargo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.shared.ValueObject;

/**
 * A handling activity represents how and where a cargo can be handled,
 * and can be used to express predictions about what is expected to
 * happen to a cargo in the future.
 *
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class HandlingActivity implements ValueObject<HandlingActivity> {

  // TODO make HandlingActivity a part of HandlingEvent too? There is some overlap. 

  @NonNull @Getter
  private HandlingEvent.Type type;
  @NonNull @Getter
  private Location location;
  @Getter
  private Voyage voyage;

  HandlingActivity() {
    // Needed by Hibernate
  }
  
}
