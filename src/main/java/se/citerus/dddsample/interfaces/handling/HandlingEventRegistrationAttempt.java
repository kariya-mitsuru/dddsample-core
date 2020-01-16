package se.citerus.dddsample.interfaces.handling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a simple transfer object for passing incoming handling event
 * registration attempts to proper the registration procedure.
 *
 * It is used as a message queue element. 
 *
 */
@RequiredArgsConstructor
public final class HandlingEventRegistrationAttempt implements Serializable {

  private final Date registrationTime;
  private final Date completionTime;
  @Getter
  private final TrackingId trackingId;
  @Getter
  private final VoyageNumber voyageNumber;
  @Getter
  private final HandlingEvent.Type type;
  @Getter
  private final UnLocode unLocode;

  public Date getCompletionTime() {
    return new Date(completionTime.getTime());
  }

  public Date getRegistrationTime() {
    return registrationTime;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
  
}
