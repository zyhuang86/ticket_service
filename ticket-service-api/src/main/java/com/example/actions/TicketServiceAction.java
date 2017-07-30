package com.example.actions;

import com.example.dataAccess.SeatDataAccess;
import com.example.datatype.SeatHold;
import com.example.datatype.SeatInformation;
import com.example.exceptions.CustomerEmailMismatchException;
import com.example.exceptions.InvalidSeatHoldIdException;
import com.example.exceptions.NotEnoughSeatsException;
import com.example.interfaces.ITicketService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class TicketServiceAction implements ITicketService {
   private final static Logger LOG = Logger.getLogger(TicketServiceAction.class);
   private SeatDataAccess seatDataAccess;
   private ExecutorServiceAction executorServiceAction;

   public TicketServiceAction(SeatDataAccess seatDataAccess,
                              ExecutorServiceAction executorServiceAction) {
      this.executorServiceAction = executorServiceAction;
      this.seatDataAccess = seatDataAccess;
   }

   public int numSeatsAvailable() {
      return seatDataAccess.getAvailableSeatCount().intValue();
   }

   public SeatHold findAndHoldSeats(int numSeats, String customerEmail) throws NotEnoughSeatsException {
      if (numSeats > numSeatsAvailable()) {
         throw new NotEnoughSeatsException();
      }
      getBestSeatsAvailable(numSeats);
      SeatHold seatHold = seatDataAccess.holdSeats(getBestSeatsAvailable(numSeats), customerEmail);
      executorServiceAction.scheduleOnHoldCleanUpTask(seatHold.getSeatHoldId());
      return seatHold;
   }

   public String reserveSeats(int seatHoldId, String customerEmail) throws InvalidSeatHoldIdException,
           CustomerEmailMismatchException {
      String message = "Reservation Complete";
      if (seatDataAccess.areSeatsStillOnHold(seatHoldId)) {
         if (customerEmail.equals(seatDataAccess.getOnHoldSeatInfoByHoldId(seatHoldId).get(0).getCustomerEmail())) {
            seatDataAccess.reserveSeats(seatHoldId, customerEmail);
            executorServiceAction.cancelScheduledOnHoldCleanUpTask(seatHoldId);
         } else {
            throw new CustomerEmailMismatchException();
         }
      } else {
         throw new InvalidSeatHoldIdException();
      }
      return message;
   }

   private List<Integer> getBestSeatsAvailable(int numSeats) {
      List<Integer> seatIdsToReturn = new ArrayList<>();
      Map<Integer, List<SeatInformation>> neighboringSeatsGroups = groupNeighboringSeats();

      // Iterate from rows closest to the stage towards the back until a group of neighboring seats
      // large enough to accommodate the requested number of seats is found
      for (Map.Entry<Integer, List<SeatInformation>> neighboringSeatGroup : neighboringSeatsGroups.entrySet()) {
         if (neighboringSeatGroup.getValue().size() >= numSeats) {
            List<Integer> seatIds = neighboringSeatGroup.getValue().stream()
                    .map(SeatInformation::getSeatId).collect(Collectors.toList());
            seatIdsToReturn = new ArrayList<>(seatIds.subList(0, numSeats));
            break;
         }
      }

      // If none of the grouped seats are large enough, find the best possible segmented
      // seating arrangement by keeping as many of the seats together as possible
      if (seatIdsToReturn.size() == 0) {
         seatIdsToReturn = getBestSegmentedSeats(neighboringSeatsGroups, numSeats);
      }

      return seatIdsToReturn;
   }

   private List<Integer> getBestSegmentedSeats(Map<Integer, List<SeatInformation>> neighboringSeatsGroups,
                                               int numSeats) {
      List<Integer> seatIdsToReturn = new ArrayList<>();
      Integer numberOfSeatsRemaining = numSeats;

      Map<Integer, List<SeatInformation>> neighboringSeatsGroupsSortedBySize = neighboringSeatsGroups.entrySet()
              .stream()
              .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
              .collect(toMap(
                      Map.Entry::getKey,
                      Map.Entry::getValue,
                      (a,b) -> {throw new AssertionError();},
                      LinkedHashMap::new));

      // The goal is to keep as many of the seats together as possible. Therefore, we start from the largest
      // size of grouped seats and work our way down. When two group of seats are identical in size, the row
      // towards the front has higher priority.
      for (Map.Entry<Integer, List<SeatInformation>> seatGroup : neighboringSeatsGroupsSortedBySize.entrySet()) {
         if (numberOfSeatsRemaining == 0) {
            break;
         } else if (seatGroup.getValue().size() <= numberOfSeatsRemaining) {
            seatIdsToReturn.addAll(seatGroup.getValue().stream()
                    .map(SeatInformation::getSeatId).collect(Collectors.toList()));
            numberOfSeatsRemaining -= seatGroup.getValue().size();
         } else {
            for (SeatInformation seatInformation : seatGroup.getValue()) {
               if (numberOfSeatsRemaining == 0) {
                  break;
               }
               seatIdsToReturn.add(seatInformation.getSeatId());
               numberOfSeatsRemaining--;
            }
         }
      }

      return seatIdsToReturn;
   }

   private Map<Integer, List<SeatInformation>> groupNeighboringSeats() {
      List<SeatInformation> availableSeats = seatDataAccess.getUnreservedSeats();
      List<SeatInformation> neighboringSeats = new ArrayList<>();
      Iterator iterator = availableSeats.iterator();
      SeatInformation previousSeatInfo = null;
      Integer groupIdIndex = 0;
      Map<Integer, List<SeatInformation>> groupedNeighboringSeats = new LinkedHashMap<>();

      while(iterator.hasNext()) {
         SeatInformation seatInformation = (SeatInformation) iterator.next();

         // whenever a break in seat sequence is detected between last and current seat
         // save the existing set of neighboring seats and create a new list to track the next
         // set of neighboring seats
         if (previousSeatInfo != null && !areNeighboringSeats(seatInformation, previousSeatInfo)) {
            groupedNeighboringSeats.putIfAbsent(groupIdIndex++, neighboringSeats);
            neighboringSeats = new ArrayList<>();
         }
         previousSeatInfo = seatInformation;
         neighboringSeats.add(seatInformation);

         // last entry
         if (!iterator.hasNext()) {
            groupedNeighboringSeats.putIfAbsent(groupIdIndex++, neighboringSeats);
         }
      }

      return groupedNeighboringSeats;
   }

   private Boolean areNeighboringSeats(SeatInformation seatInformationA, SeatInformation seatInformationB) {
      Boolean areNeighbors = false;
      if (seatInformationA.getRowNumber().equals(seatInformationB.getRowNumber()) &&
              seatInformationA.getColumnNumber().equals(seatInformationB.getColumnNumber() + 1)) {
         areNeighbors = true;
      }
      return areNeighbors;
   }
}
