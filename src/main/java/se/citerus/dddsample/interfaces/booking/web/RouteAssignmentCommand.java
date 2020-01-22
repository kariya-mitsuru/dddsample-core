package se.citerus.dddsample.interfaces.booking.web;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.ListUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class RouteAssignmentCommand {

  private String trackingId;
  private List<LegCommand> legs = ListUtils.lazyList(
    new ArrayList<LegCommand>(), LegCommand.factory()
  );

  @Getter @Setter
  public static final class LegCommand {
    private String voyageNumber;
    private String fromUnLocode;
    private String toUnLocode;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDate fromDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDate toDate;

    public static Factory<LegCommand> factory() {
      return LegCommand::new;
    }

  }
}
