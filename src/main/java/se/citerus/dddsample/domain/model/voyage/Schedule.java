package se.citerus.dddsample.domain.model.voyage;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.Collections;
import java.util.List;

/**
 * A voyage schedule.
 * 
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Schedule implements ValueObject<Schedule> {

  private List<CarrierMovement> carrierMovements = Collections.emptyList();

  public static final Schedule EMPTY = new Schedule();

  Schedule(final List<CarrierMovement> carrierMovements) {
    Validate.notNull(carrierMovements);
    Validate.noNullElements(carrierMovements);
    Validate.notEmpty(carrierMovements);

    this.carrierMovements = carrierMovements;
  }

  /**
   * @return Carrier movements.
   */
  public List<CarrierMovement> carrierMovements() {
    return Collections.unmodifiableList(carrierMovements);
  }
}
