package com.example.services;

import com.example.actions.TicketServiceAction;
import com.example.exceptions.CustomerEmailMismatchException;
import com.example.exceptions.InvalidSeatHoldIdException;
import com.example.exceptions.NotEnoughSeatsException;
import io.dropwizard.jersey.errors.ErrorMessage;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/ticketService")
@Produces(MediaType.APPLICATION_JSON)
public class TicketService {
   private final static Logger LOG = Logger.getLogger(TicketService.class.getName());

   private TicketServiceAction ticketServiceAction;
   private final static String EMAIL_REGEX_PATTERN = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                   + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
   private final static String INVALID_EMAIL_MESSAGE = ": Invalid Email Received";

   public TicketService(TicketServiceAction ticketServiceAction) {
      this.ticketServiceAction = ticketServiceAction;
   }

   @GET
   @Path("/availableSeatCount")
   public Response getAvailableSeatCount() {
      return Response.ok(ticketServiceAction.numSeatsAvailable()).build();
   }

   @PUT
   @Path("/findAndHoldSeats")
   public Response findAndHoldSeats(@QueryParam("numberOfSeats") @NotNull String numberOfSeats,
                                    @QueryParam("email")
                                    @Pattern(regexp = EMAIL_REGEX_PATTERN,
                                             message = INVALID_EMAIL_MESSAGE) String customerEmail) {
      Response response;
      String logStringFormat = "%s. numberOfSeats: %s customerEmail: %s";
      try {
         response = Response.ok(ticketServiceAction.findAndHoldSeats(Integer.valueOf(numberOfSeats),
                 customerEmail)).build();
         LOG.info(String.format(logStringFormat, "findAndHoldSeats", numberOfSeats, customerEmail));
      } catch (NotEnoughSeatsException exception) {
         response = Response.status(Response.Status.BAD_REQUEST)
                 .entity(new ErrorMessage(exception.getMessage())).build();
         LOG.warning(String.format(logStringFormat, exception.getMessage(), numberOfSeats, customerEmail));

      } catch (NumberFormatException exception) {
         String message = "Invalid number of seats to hold received";
         response = Response.status(Response.Status.BAD_REQUEST)
                 .entity(new ErrorMessage(message)).build();
         LOG.warning(String.format(logStringFormat, message, numberOfSeats, customerEmail));
      }
      return response;
   }

   @PUT
   @Path("/findAndReserveSeats")
   public Response findAndReserveSeats(@QueryParam("seatHoldId") @NotNull String seatHoldId,
                                       @QueryParam("email")
                                       @Pattern(regexp = EMAIL_REGEX_PATTERN,
                                                message = INVALID_EMAIL_MESSAGE) String customerEmail) {
      Response response;
      String logStringFormat = "%s. seatHoldId: %s customerEmail: %s";
      try {
         response = Response.ok(ticketServiceAction.reserveSeats(Integer.valueOf(seatHoldId),
                 customerEmail)).build();
         LOG.info(String.format(logStringFormat, "findAndReserveSeats", seatHoldId, customerEmail));
      } catch (NumberFormatException exception) {
         String message = "Invalid seat hold id received";
         response = Response.status(Response.Status.BAD_REQUEST)
                 .entity(new ErrorMessage(message)).build();
         LOG.warning(String.format(logStringFormat, message, seatHoldId, customerEmail));
      } catch (InvalidSeatHoldIdException | CustomerEmailMismatchException exception) {
         response = Response.status(Response.Status.BAD_REQUEST)
                 .entity(new ErrorMessage(exception.getMessage())).build();
         LOG.warning(String.format(logStringFormat, exception.getMessage(), seatHoldId,
                 customerEmail));
      }
      return response;
   }
}
