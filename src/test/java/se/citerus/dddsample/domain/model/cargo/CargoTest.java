package se.citerus.dddsample.domain.model.cargo;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.MISROUTED;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.NOT_ROUTED;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.ROUTED;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.NOT_RECEIVED;
import static se.citerus.dddsample.domain.model.location.SampleLocations.GOTHENBURG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HAMBURG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HANGZOU;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.MELBOURNE;
import static se.citerus.dddsample.domain.model.location.SampleLocations.NEWYORK;
import static se.citerus.dddsample.domain.model.location.SampleLocations.ROTTERDAM;
import static se.citerus.dddsample.domain.model.location.SampleLocations.SHANGHAI;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;
import static se.citerus.dddsample.domain.model.location.SampleLocations.TOKYO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.citerus.dddsample.application.util.DateTestUtil;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

public class CargoTest {

  private List<HandlingEvent> events;
  private Voyage voyage;

  @Before
  public void setUp() {
    events = new ArrayList<HandlingEvent>();

    voyage = new Voyage.Builder(new VoyageNumber("0123"), STOCKHOLM).
      addMovement(HAMBURG, LocalDateTime.now(), LocalDateTime.now()).
      addMovement(HONGKONG, LocalDateTime.now(), LocalDateTime.now()).
      addMovement(MELBOURNE, LocalDateTime.now(), LocalDateTime.now()).
      build();
  }

  @Test
  public void testConstruction() {
    final TrackingId trackingId = new TrackingId("XYZ");
    final LocalDateTime arrivalDeadline = DateTestUtil.toDate("2009-03-13");
    final RouteSpecification routeSpecification = new RouteSpecification(
      STOCKHOLM, MELBOURNE, arrivalDeadline
    );

    final Cargo cargo = new Cargo(trackingId, routeSpecification);

    assertThat(cargo.delivery().routingStatus()).isEqualTo(NOT_ROUTED);
    assertThat(cargo.delivery().transportStatus()).isEqualTo(NOT_RECEIVED);
    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(Location.UNKNOWN);
    assertThat(cargo.delivery().currentVoyage()).isEqualTo(Voyage.NONE);    
  }

  @Test
  public void testRoutingStatus() {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, LocalDateTime.now()));
    final Itinerary good = new Itinerary();
    final Itinerary bad = new Itinerary();
    final RouteSpecification acceptOnlyGood = new RouteSpecification(cargo.origin(), cargo.routeSpecification().destination(), LocalDateTime.now()) {
      @Override
      public boolean isSatisfiedBy(Itinerary itinerary) {
        return itinerary == good;
      }
    };

    cargo.specifyNewRoute(acceptOnlyGood);

    assertThat(cargo.delivery().routingStatus()).isEqualTo(NOT_ROUTED);
    
    cargo.assignToRoute(bad);
    assertThat(cargo.delivery().routingStatus()).isEqualTo(MISROUTED);

    cargo.assignToRoute(good);
    assertThat(cargo.delivery().routingStatus()).isEqualTo(ROUTED);
  }

  @Test
  public void testlastKnownLocationUnknownWhenNoEvents() {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, LocalDateTime.now()));

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(Location.UNKNOWN);
  }

  @Test
  public void testlastKnownLocationReceived() throws Exception {
    Cargo cargo = populateCargoReceivedStockholm();

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(STOCKHOLM);
  }

  @Test
  public void testlastKnownLocationClaimed() throws Exception {
    Cargo cargo = populateCargoClaimedMelbourne();

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(MELBOURNE);
  }

  @Test
  public void testlastKnownLocationUnloaded() throws Exception {
    Cargo cargo = populateCargoOffHongKong();

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(HONGKONG);
  }

  @Test
  public void testlastKnownLocationloaded() throws Exception {
    Cargo cargo = populateCargoOnHamburg();

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(HAMBURG);
  }

  @Test
  public void testEquality() {
    RouteSpecification spec1 = new RouteSpecification(STOCKHOLM, HONGKONG, LocalDateTime.now());
    RouteSpecification spec2 = new RouteSpecification(STOCKHOLM, MELBOURNE, LocalDateTime.now());
    Cargo c1 = new Cargo(new TrackingId("ABC"), spec1);
    Cargo c2 = new Cargo(new TrackingId("CBA"), spec1);
    Cargo c3 = new Cargo(new TrackingId("ABC"), spec2);
    Cargo c4 = new Cargo(new TrackingId("ABC"), spec1);

    assertThat(c1.equals(c4)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
    assertThat(c1.equals(c3)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
    assertThat(c3.equals(c4)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
    assertThat(c1.equals(c2)).as("Cargos are not equal when TrackingID differ").isFalse();
  }

  @Test
  public void testIsUnloadedAtFinalDestination() {
    Cargo cargo = setUpCargoWithItinerary(HANGZOU, TOKYO, NEWYORK);
    assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

    // Adding an event unrelated to unloading at final destination
    events.add(
      new HandlingEvent(cargo, ts(10), LocalDateTime.now(), HandlingEvent.Type.RECEIVE, HANGZOU));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

    Voyage voyage = new Voyage.Builder(new VoyageNumber("0123"), HANGZOU).
      addMovement(NEWYORK, LocalDateTime.now(), LocalDateTime.now()).
      build();

    // Adding an unload event, but not at the final destination
    events.add(
      new HandlingEvent(cargo, ts(20), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, TOKYO, voyage));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

    // Adding an event in the final destination, but not unload
    events.add(
      new HandlingEvent(cargo, ts(30), LocalDateTime.now(), HandlingEvent.Type.CUSTOMS, NEWYORK));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

    // Finally, cargo is unloaded at final destination
    events.add(
      new HandlingEvent(cargo, ts(40), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, NEWYORK, voyage));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isUnloadedAtDestination()).isTrue();
  }

  // TODO: Generate test data some better way
  private Cargo populateCargoReceivedStockholm() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, LocalDateTime.now()));

    HandlingEvent he = new HandlingEvent(cargo, getDate("2007-12-01"), LocalDateTime.now(), HandlingEvent.Type.RECEIVE, STOCKHOLM);
    events.add(he);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    return cargo;
  }

  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();

    events.add(new HandlingEvent(cargo, getDate("2007-12-09"), LocalDateTime.now(), HandlingEvent.Type.CLAIM, MELBOURNE));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    return cargo;
  }

  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, LocalDateTime.now()));


    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), LocalDateTime.now(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), LocalDateTime.now(), HandlingEvent.Type.LOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-04"), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, HONGKONG, voyage));

    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, LocalDateTime.now()));

    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), LocalDateTime.now(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), LocalDateTime.now(), HandlingEvent.Type.LOAD, HAMBURG, voyage));

    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, LocalDateTime.now()));

    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), LocalDateTime.now(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), LocalDateTime.now(), HandlingEvent.Type.LOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-04"), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, HONGKONG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-05"), LocalDateTime.now(), HandlingEvent.Type.LOAD, HONGKONG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-07"), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, MELBOURNE, voyage));

    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    return cargo;
  }

  private Cargo populateCargoOnHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, LocalDateTime.now()));

    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), LocalDateTime.now(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), LocalDateTime.now(), HandlingEvent.Type.LOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-04"), LocalDateTime.now(), HandlingEvent.Type.UNLOAD, HONGKONG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-05"), LocalDateTime.now(), HandlingEvent.Type.LOAD, HONGKONG, voyage));

    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    return cargo;
  }

  @Test
  public void testIsMisdirected() {
    //A cargo with no itinerary is not misdirected
    Cargo cargo = new Cargo(new TrackingId("TRKID"), new RouteSpecification(SHANGHAI, GOTHENBURG, LocalDateTime.now()));
    assertThat(cargo.delivery().isMisdirected()).isFalse();

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);

    //A cargo with no handling events is not misdirected
    assertThat(cargo.delivery().isMisdirected()).isFalse();

    Collection<HandlingEvent> handlingEvents = new ArrayList<HandlingEvent>();

    //Happy path
    handlingEvents.add(new HandlingEvent(cargo, ts(10), ts(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    handlingEvents.add(new HandlingEvent(cargo, ts(30), ts(40), HandlingEvent.Type.LOAD, SHANGHAI, voyage));
    handlingEvents.add(new HandlingEvent(cargo, ts(50), ts(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, ts(70), ts(80), HandlingEvent.Type.LOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, ts(90), ts(100), HandlingEvent.Type.UNLOAD, GOTHENBURG, voyage));
    handlingEvents.add(new HandlingEvent(cargo, ts(110), ts(120), HandlingEvent.Type.CLAIM, GOTHENBURG));
    handlingEvents.add(new HandlingEvent(cargo, ts(130), ts(140), HandlingEvent.Type.CUSTOMS, GOTHENBURG));

    events.addAll(handlingEvents);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isMisdirected()).isFalse();

    //Try a couple of failing ones

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    handlingEvents = new ArrayList<HandlingEvent>();

    handlingEvents.add(new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.RECEIVE, HANGZOU));
    events.addAll(handlingEvents);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    assertThat(cargo.delivery().isMisdirected()).isTrue();


    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    handlingEvents = new ArrayList<HandlingEvent>();

    handlingEvents.add(new HandlingEvent(cargo, ts(10), ts(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    handlingEvents.add(new HandlingEvent(cargo, ts(30), ts(40), HandlingEvent.Type.LOAD, SHANGHAI, voyage));
    handlingEvents.add(new HandlingEvent(cargo, ts(50), ts(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, ts(70), ts(80), HandlingEvent.Type.LOAD, ROTTERDAM, voyage));

    events.addAll(handlingEvents);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    assertThat(cargo.delivery().isMisdirected()).isTrue();


    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    handlingEvents = new ArrayList<HandlingEvent>();

    handlingEvents.add(new HandlingEvent(cargo, ts(10), ts(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    handlingEvents.add(new HandlingEvent(cargo, ts(30), ts(40), HandlingEvent.Type.LOAD, SHANGHAI, voyage));
    handlingEvents.add(new HandlingEvent(cargo, ts(50), ts(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, LocalDateTime.now(), LocalDateTime.now(), HandlingEvent.Type.CLAIM, ROTTERDAM));

    events.addAll(handlingEvents);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    assertThat(cargo.delivery().isMisdirected()).isTrue();
  }

  private Cargo setUpCargoWithItinerary(Location origin, Location midpoint, Location destination) {
    Cargo cargo = new Cargo(new TrackingId("CARGO1"), new RouteSpecification(origin, destination, LocalDateTime.now()));

    Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(voyage, origin, midpoint, LocalDateTime.now(), LocalDateTime.now()),
        new Leg(voyage, midpoint, destination, LocalDateTime.now(), LocalDateTime.now())
      )
    );

    cargo.assignToRoute(itinerary);
    return cargo;
  }

  /**
   * Parse an ISO 8601 (YYYY-MM-DD) String to LocalDateTime
   *
   * @param isoFormat String to parse.
   * @return Created date instance.
   * @throws DateTimeParseException Thrown if parsing fails.
   */
  private static LocalDateTime getDate(String isoFormat) throws DateTimeParseException {
    final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    return dateFormat.parse(isoFormat);
  }

  private static LocalDateTime ts(long ms) {
    return LocalDate.ofEpochSecond(0, ms * 1000 * 1000, ZoneId.systemDefault());
}
