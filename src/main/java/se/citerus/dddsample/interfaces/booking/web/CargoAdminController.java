package se.citerus.dddsample.interfaces.booking.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import se.citerus.dddsample.interfaces.booking.facade.BookingServiceFacade;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles cargo booking and routing. Operates against a dedicated remoting service facade,
 * and could easily be rewritten as a thick Swing client. Completely separated from the domain layer,
 * unlike the tracking user interface.
 * <p>
 * In order to successfully keep the domain model shielded from user interface considerations,
 * this approach is generally preferred to the one taken in the tracking controller. However,
 * there is never any one perfect solution for all situations, so we've chosen to demonstrate
 * two polarized ways to build user interfaces.
 *
 * @see se.citerus.dddsample.interfaces.tracking.CargoTrackingController
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public final class CargoAdminController {

    private final BookingServiceFacade bookingServiceFacade;

	/*
    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(LocalDateTime.class, new CustomDateEditor(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"), false));
    }
	*/

    @RequestMapping("/registration")
    public String registration(Map<String, Object> model) throws Exception {
        List<LocationDTO> dtoList = bookingServiceFacade.listShippingLocations();

        List<String> unLocodeStrings = new ArrayList<String>();

        for (LocationDTO dto : dtoList) {
            unLocodeStrings.add(dto.getUnLocode());
        }

        model.put("unlocodes", unLocodeStrings);
        model.put("locations", dtoList);
        return "admin/registrationForm";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(RegistrationCommand command) throws Exception {
        LocalDateTime arrivalDeadline = LocalDate.parse(command.getArrivalDeadline(), DateTimeFormatter.ofPattern("dd/MM/uuuu")).atStartOfDay();
        String trackingId = bookingServiceFacade.bookNewCargo(
                command.getOriginUnlocode(), command.getDestinationUnlocode(), arrivalDeadline
        );
        return "redirect:show?trackingId=" + trackingId;
    }

    @RequestMapping("/list")
    public String list(Map<String, Object> model) throws Exception {
        List<CargoRoutingDTO> cargoList = bookingServiceFacade.listAllCargos();

        model.put("cargoList", cargoList);
        return "admin/list";
    }

    @RequestMapping("/show")
    public String show(@RequestParam String trackingId, Map<String, Object> model) throws Exception {
        CargoRoutingDTO dto = bookingServiceFacade.loadCargoForRouting(trackingId);
        model.put("cargo", dto);
        return "admin/show";
    }

    @RequestMapping("/selectItinerary")
    public String selectItinerary(@RequestParam String trackingId, Map<String, Object> model) throws Exception {

        List<RouteCandidateDTO> routeCandidates = bookingServiceFacade.requestPossibleRoutesForCargo(trackingId);
        model.put("routeCandidates", routeCandidates);

        CargoRoutingDTO cargoDTO = bookingServiceFacade.loadCargoForRouting(trackingId);
        model.put("cargo", cargoDTO);

        return "admin/selectItinerary";
    }

    @RequestMapping(value = "/assignItinerary", method = RequestMethod.POST)
    public String assignItinerary(RouteAssignmentCommand command) throws Exception {
        List<LegDTO> legDTOs = new ArrayList<LegDTO>(command.getLegs().size());
        for (RouteAssignmentCommand.LegCommand leg : command.getLegs()) {
            legDTOs.add(new LegDTO(
                            leg.getVoyageNumber(),
                            leg.getFromUnLocode(),
                            leg.getToUnLocode(),
                            leg.getFromDate().atStartOfDay(),
                            leg.getToDate().atStartOfDay())
            );
        }

        RouteCandidateDTO selectedRoute = new RouteCandidateDTO(legDTOs);

        bookingServiceFacade.assignCargoToRoute(command.getTrackingId(), selectedRoute);

        return "redirect:show?trackingId=" + command.getTrackingId();
    }

    @RequestMapping(value = "/pickNewDestination")
    public String pickNewDestination(@RequestParam String trackingId, Map<String, Object> model) throws Exception {
        List<LocationDTO> locations = bookingServiceFacade.listShippingLocations();
        model.put("locations", locations);

        CargoRoutingDTO cargo = bookingServiceFacade.loadCargoForRouting(trackingId);
        model.put("cargo", cargo);

        return "admin/pickNewDestination";
    }

    @RequestMapping(value = "/changeDestination", method = RequestMethod.POST)
    public String changeDestination(@RequestParam String trackingId, @RequestParam String unlocode) throws Exception {
        System.out.println("trackingId: " + trackingId + ", unlocode: " + unlocode);
        bookingServiceFacade.changeDestination(trackingId, unlocode);
        return "redirect:show?trackingId=" + trackingId;
    }
}
