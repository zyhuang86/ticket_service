package com.example.actions;

import com.example.dataAccess.SeatDataAccess;
import com.example.datatype.SeatHold;
import com.example.interfaces.ITicketService;

import java.util.Arrays;

public class TicketServiceAction implements ITicketService {
   private SeatDataAccess seatDataAccess;
   public TicketServiceAction(SeatDataAccess seatDataAccess) {
      this.seatDataAccess = seatDataAccess;
   }

   public int numSeatsAvailable() {
      return seatDataAccess.getAvailableSeatCount().intValue();
   }

   public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
      return seatDataAccess.holdSeats(Arrays.asList(1, 2, 3), customerEmail);
   }

   public String reserveSeats(int seatHoldId, String customerEmail) {
      String message = "Reservation Complete";
      seatDataAccess.reserveSeats(seatHoldId, customerEmail);
      return message;
   }
}
