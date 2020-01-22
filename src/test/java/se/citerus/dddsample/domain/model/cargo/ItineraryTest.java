package se.citerus.dddsample.domain.model.cargo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static se.citerus.dddsample.domain.model.location.SampleLocations.GOTHENBURG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HANGZOU;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HELSINKI;
import static se.citerus.dddsample.domain.model.location.SampleLocations.NEWYORK;
import static se.citerus.dddsample.domain.model.location.SampleLocations.ROTTERDAM;
import static se.citerus.dddsample.domain.model.location.SampleLocations.SHANGHAI;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

public class ItineraryTest {
  private final CarrierMovement abc = new CarrierMovement(SHANGHAI, ROTTERDAM, LocalDateTime.now(), LocalDateTime.now());
  private final CarrierMovement def = new CarrierMovement(ROTTERDAM, GOTHENBURG, LocalDateTime.now(), LocalDateTime.now());
  private final CarrierMovement ghi = new CarrierMovement(ROTTERDAM, NEWYORK, LocalDateTime.now(), LocalDateTime.now());
  private final CarrierMovement jkl = new CarrierMovement(SHANGHAI, HELSINKI, LocalDateTime.now(), LocalDateTime.now());

  Voyage voyage, wrongVoyage;

  @Before
  public void setUp() {
    voyage = new Voyage.Builder(new VoyageNumber("0123"), SHANGHAI).
      addMovement(ROTTERDAM, LocalDateTime.now(), LocalDateTime.now()).
      addMovement(GOTHENBURG, LocalDateTime.now(), LocalDateTime.now()).
      build();

    wrongVoyage = new Voyage.Builder(new VoyageNumber("666"), NEWYORK).
      addMovement(STOCKHOLM, LocalDateTime.now(), LocalDateTime.now()).
      addMovement(HELSINKI, LocalDateTime.now(), LocalDateTime.now()).
      build();
  }

  @Test
  public void testCargoOnTrack() {

    TrackingId trackingId = new TrackingId("CARGO1");
    RouteSpecification routeSpecification = new RouteSpecification(SHANGHAI, GOTHENBURG, LocalDateTime.now());
    Cargo cargo = new Cargo(trackingId, routeSpecification);

    Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(voyage, SHANGHAI, ROTTERDAM, LocalDateTime.now(), LocalDateTime.now()),
        new Leg(voyage, ROTTERDAM, GOTHENBURG, LocalDateTime.now(), LocalDateTime.now())
      )
    );

    //Happy path
    HandlingEvent event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.RECEIVE, SHANGHAI);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.LOAD, SHANGHAI, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.LOAD, ROTTERDAM, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, GOTHENBURG, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.CLAIM, GOTHENBURG);
    assertThat(itinerary.isExpected(event)).isTrue();

    //Customs event changes nothing
    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.CUSTOMS, GOTHENBURG);
    assertThat(itinerary.isExpected(event)).isTrue();

    //Received at the wrong location
    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.RECEIVE, HANGZOU);
    assertThat(itinerary.isExpected(event)).isFalse();

    //Loaded to onto the wrong ship, correct location
    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.LOAD, ROTTERDAM, wrongVoyage);
    assertThat(itinerary.isExpected(event)).isFalse();

    //Unloaded from the wrong ship in the wrong location
    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, HELSINKI, wrongVoyage);
    assertThat(itinerary.isExpected(event)).isFalse();

    event = new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.CLAIM, ROTTERDAM);
    assertThat(itinerary.isExpected(event)).isFalse();

  }
  @Test
  public void testNextExpectedEvent() {

  }
  @Test
  public void testCreateItinerary() {
    try {
      new Itinerary(new ArrayList<>());
      fail("An empty itinerary is not OK");
    } catch (IllegalArgumentException iae) {
      //Expected
    }

    try {
      List<Leg> legs = null;
      new Itinerary(legs);
      fail("Null itinerary is not OK");
    } catch (NullPointerException iae) {
      //Expected
    }
  }

}
