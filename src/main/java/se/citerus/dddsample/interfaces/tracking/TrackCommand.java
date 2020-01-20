package se.citerus.dddsample.interfaces.tracking;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public final class TrackCommand {

  /**
   * The tracking id.
   */
  @Getter @Setter
  private String trackingId;

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
  }
}
