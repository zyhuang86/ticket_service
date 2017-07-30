package com.example.actions;

import com.example.dataAccess.SeatDataAccess;
import com.example.datatype.SeatHold;
import com.example.datatype.SeatInformation;
import com.example.exceptions.NotEnoughSeatsException;
import com.example.interfaces.ITicketService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class TicketServiceAction implements ITicketService {
   private SeatDataAccess seatDataAccess;
   public TicketServiceAction(SeatDataAccess seatDataAccess) {
      this.seatDataAccess = seatDataAccess;
   }

   public int numSeatsAvailable() {
      return seatDataAccess.getAvailableSeatCount().intValue();
   }

   public SeatHold findAndHoldSeats(int numSeats, String customerEmail) throws NotEnoughSeatsException {
      if (numSeats > numSeatsAvailable()) {
         throw new NotEnoughSeatsException("Number of seats to put on hold exceeds available seat count");
      }
      getBestSeatsAvailable(numSeats);
      return seatDataAccess.holdSeats(getBestSeatsAvailable(numSeats), customerEmail);
   }

   public String reserveSeats(int seatHoldId, String customerEmail) {
      String message = "Reservation Complete";
      seatDataAccess.reserveSeats(seatHoldId, customerEmail);
      return message;
   }

   private List<Integer> getBestSeatsAvailable(int numSeats) {
      List<Integer> seatIdsToReturn = new ArrayList<>();
      Map<Integer, List<SeatInformation>> neighboringSeatsGroups = groupedNeighboringSeats();

      for (Map.Entry<Integer, List<SeatInformation>> neighboringSeatGroup : neighboringSeatsGroups.entrySet()) {
         if (neighboringSeatGroup.getValue().size() >= numSeats) {
            List<Integer> seatIds = neighboringSeatGroup.getValue().stream()
                    .map(SeatInformation::getSeatId).collect(Collectors.toList());
            seatIdsToReturn = new ArrayList<>(seatIds.subList(0, numSeats));
            break;
         }
      }

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

   private Map<Integer, List<SeatInformation>> groupedNeighboringSeats() {
      List<SeatInformation> availableSeats = seatDataAccess.getUnreservedSeats();
      List<SeatInformation> neighboringSeats = new ArrayList<>();
      Iterator iterator = availableSeats.iterator();
      SeatInformation previousSeatInfo = null;
      Integer groupIdIndex = 0;
      Map<Integer, List<SeatInformation>> groupedNeighboringSeats = new LinkedHashMap<>();

      availableSeats.stream().sorted(Comparator.comparing(SeatInformation::getSeatId));
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
      if (seatInformationA.getRowNumber() == seatInformationB.getRowNumber() &&
              seatInformationA.getColumnNumber() == seatInformationB.getColumnNumber() + 1) {
         areNeighbors = true;
      }
      return areNeighbors;
   }

}
