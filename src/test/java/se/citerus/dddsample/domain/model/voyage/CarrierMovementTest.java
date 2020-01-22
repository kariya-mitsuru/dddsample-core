package se.citerus.dddsample.domain.model.voyage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HAMBURG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;

import java.time.LocalDateTime;

import org.junit.Test;

public class CarrierMovementTest {

  @Test
  public void testConstructor() {
    try {
      new CarrierMovement(null, null, LocalDateTime.now(), LocalDateTime.now());
      fail("Should not accept null constructor arguments");
    } catch (NullPointerException expected) {}

    try {
      new CarrierMovement(STOCKHOLM, null, LocalDateTime.now(), LocalDateTime.now());
      fail("Should not accept null constructor arguments");
    } catch (NullPointerException expected) {}

    // Legal
    new CarrierMovement(STOCKHOLM, HAMBURG, LocalDateTime.now(), LocalDateTime.now());
  }

  @Test
  public void testSameValueAsEqualsHashCode() {
    final LocalDateTime referenceTime = LocalDateTime.now();

    // One could, in theory, use the same LocalDateTime(referenceTime) for all of these movements
    // However, in practice, carrier movements will be initialized by different processes
    // so we might have different LocalDateTime that reference the same time, and we want to be
    // certain that sameValueAs does the right thing in that case.
    CarrierMovement cm1 = new CarrierMovement(STOCKHOLM, HAMBURG, referenceTime, referenceTime);
    CarrierMovement cm2 = new CarrierMovement(STOCKHOLM, HAMBURG, referenceTime, referenceTime);
    CarrierMovement cm3 = new CarrierMovement(HAMBURG, STOCKHOLM, referenceTime, referenceTime);
    CarrierMovement cm4 = new CarrierMovement(HAMBURG, STOCKHOLM, referenceTime, referenceTime);

    assertThat(cm1.sameValueAs(cm2)).isTrue();
    assertThat(cm2.sameValueAs(cm3)).isFalse();
    assertThat(cm3.sameValueAs(cm4)).isTrue();
    
    assertThat(cm1.equals(cm2)).isTrue();
    assertThat(cm2.equals(cm3)).isFalse();
    assertThat(cm3.equals(cm4)).isTrue();

    assertThat(cm1.hashCode() == cm2.hashCode()).isTrue();
    assertThat(cm2.hashCode() == cm3.hashCode()).isFalse();
    assertThat(cm3.hashCode() == cm4.hashCode()).isTrue();
  }

}
