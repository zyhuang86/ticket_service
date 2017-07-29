package com.example.services;

import com.example.actions.TicketServiceAction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ticketService")
@Produces(MediaType.APPLICATION_JSON)
public class TicketService {
   private TicketServiceAction ticketServiceAction;

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
   public Response findAndHoldSeats(@QueryParam("numberOfSeats") Integer numberOfSeats,
                                    @QueryParam("email") String customerEmail) {
      return Response.ok(ticketServiceAction.findAndHoldSeats(numberOfSeats, customerEmail)).build();
   }

   @PUT
   @Path("/findAndReserveSeats")
   public Response findAndReserveSeats(@QueryParam("holdId") Integer holdId,
                                       @QueryParam("email") String customerEmail) {
      return Response.ok(ticketServiceAction.reserveSeats(holdId, customerEmail)).build();
   }
}
