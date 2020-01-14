package se.citerus.dddsample.domain.model.location;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.Validate;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.regex.Pattern;

/**
 * United nations location code.
 * <p/>
 * http://www.unece.org/cefact/locode/
 * http://www.unece.org/cefact/locode/DocColumnDescription.htm#LOCODE
 */
@EqualsAndHashCode
public final class UnLocode implements ValueObject<UnLocode> {

  private String unlocode;

  // Country code is exactly two letters.
  // Location code is usually three letters, but may contain the numbers 2-9 as well
  private static final Pattern VALID_PATTERN = Pattern.compile("[a-zA-Z]{2}[a-zA-Z2-9]{3}");

  /**
   * Constructor.
   *
   * @param countryAndLocation Location string.
   */
  public UnLocode(final String countryAndLocation) {
    Validate.notNull(countryAndLocation, "Country and location may not be null");
    Validate.isTrue(VALID_PATTERN.matcher(countryAndLocation).matches(),
      countryAndLocation + " is not a valid UN/LOCODE (does not match pattern)");

    this.unlocode = countryAndLocation.toUpperCase();
  }

  /**
   * @return country code and location code concatenated, always upper case.
   */
  public String idString() {
    return unlocode;
  }

  @Override
  public String toString() {
    return idString();
  }

  UnLocode() {
    // Needed by Hibernate
  }

}
