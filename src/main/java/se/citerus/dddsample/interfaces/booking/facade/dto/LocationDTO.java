package se.citerus.dddsample.interfaces.booking.facade.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.io.Serializable;

/**
 * Location DTO.
 */
@Getter
@RequiredArgsConstructor
public class LocationDTO implements Serializable {

  private final String unLocode;
  private final String name;
}
