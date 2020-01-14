package se.citerus.dddsample.domain.model.voyage;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import se.citerus.dddsample.domain.shared.ValueObject;

/**
 * Identifies a voyage.
 * 
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class VoyageNumber implements ValueObject<VoyageNumber> {

  @NonNull
  private String number;

  @Override
  public String toString() {
    return number;
  }

  public String idString() {
    return number;
  }
}
