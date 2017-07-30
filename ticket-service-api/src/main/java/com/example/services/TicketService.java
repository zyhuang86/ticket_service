package com.example.services;

import com.example.actions.TicketServiceAction;
import com.example.exceptions.CustomerEmailMismatchException;
import com.example.exceptions.InvalidSeatHoldIdException;
import com.example.exceptions.NotEnoughSeatsException;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ticketService")
@Produces(MediaType.APPLICATION_JSON)
public class TicketService {
   private final static Logger LOG = Logger.getLogger(TicketService.class);

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
      try {
         response = Response.ok(ticketServiceAction.findAndHoldSeats(Integer.valueOf(numberOfSeats),
                 customerEmail)).build();
      } catch (NotEnoughSeatsException exception) {
         response = Response.status(Response.Status.BAD_REQUEST)
                 .entity(new ErrorMessage(exception.getMessage())).build();
         LOG.warn(exception.getStackTrace());

      } catch (NumberFormatException exception) {
         String message = "Invalid number of seats to hold received";
         response = Response.status(Response.Status.BAD_REQUEST)
                 .entity(new ErrorMessage(message)).build();
         LOG.warn(message);
         LOG.warn(exception.getStackTrace());
      }
      return response;
   }

   @PUT
   @Path("/findAndReserveSeats")
   public Response findAndReserveSeats(@QueryParam("holdId") @NotNull String holdId,
                                       @QueryParam("email")
                                       @Pattern(regexp = EMAIL_REGEX_PATTERN,
                                                message = INVALID_EMAIL_MESSAGE) String customerEmail) {
      Response response;
      try {
         response = Response.ok(ticketServiceAction.reserveSeats(Integer.valueOf(holdId),
                 customerEmail)).build();
      } catch (NumberFormatException exception) {
         String message = "Invalid seat hold id received";
         response = Response.status(Response.Status.BAD_REQUEST)
                 .entity(new ErrorMessage(message)).build();
         LOG.warn(message);
         LOG.warn(exception.getStackTrace());
      } catch (InvalidSeatHoldIdException | CustomerEmailMismatchException exception) {
         response = Response.status(Response.Status.BAD_REQUEST)
                 .entity(new ErrorMessage(exception.getMessage())).build();
         LOG.warn(exception.getStackTrace());
      }
      return response;
   }
}
