package com.example.dataAccess;

import com.example.datatype.SeatHold;
import com.example.datatype.SeatInformation;
import com.example.datatype.SeatReservation;
import com.example.data.SeatData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SeatDataAccess {
   private Map<Integer, SeatReservation> seatAssignmentMap = SeatData.getInstance().getSeatAssignmentMap();
   private Map<Integer, List<Integer>> seatsOnHoldMap = SeatData.getInstance().getSeatsOnHoldMap();

   public SeatHold holdSeats(List<Integer> seatIdsToHold, String customerEmail) {
      Integer seatHoldId = SeatData.getInstance().getNextSeatHoldIdIndex();

      // put seats on hold
      seatsOnHoldMap.put(seatHoldId, seatIdsToHold);
      seatIdsToHold.forEach(seatId -> {
         seatAssignmentMap.get(seatId).setIsOnHold(true);
         seatAssignmentMap.get(seatId).setCustomerEmail(customerEmail);
      });

      // return hold id and seat information
      SeatHold seatHold = new SeatHold();
      seatHold.setSeatHoldId(seatHoldId);
      seatHold.setSeatInformationList(SeatData.getInstance().getSeatInformation(seatIdsToHold));
      return seatHold;
   }

   public Boolean areSeatsStillOnHold(Integer seatHoldId) {
      return seatsOnHoldMap.containsKey(seatHoldId);
   }

   public void reserveSeats(Integer seatHoldId, String customerEmail) {
      List<Integer> seatIdsToHold = seatsOnHoldMap.get(seatHoldId);
      seatIdsToHold.forEach(seatId -> {
         seatAssignmentMap.get(seatId).setIsOnHold(false);
         seatAssignmentMap.get(seatId).setIsReserved(true);
         seatAssignmentMap.get(seatId).setCustomerEmail(customerEmail);
      });
   }

   public void unholdSeats(Integer seatHoldId) {
      List<Integer> seatIdsToHold = seatsOnHoldMap.get(seatHoldId);
      seatIdsToHold.forEach(seatId ->{
         seatAssignmentMap.get(seatId).setIsOnHold(false);
         seatAssignmentMap.get(seatId).setIsReserved(false);
         seatAssignmentMap.get(seatId).setCustomerEmail("");
      });
      seatsOnHoldMap.remove(seatHoldId);
   }

   public List<SeatInformation> getUnreservedSeats() {
       List<Integer> unreservedSeatIds = seatAssignmentMap.entrySet().stream()
               .filter(seatAssignment ->
                       !seatAssignment.getValue().getIsOnHold() &&
                       !seatAssignment.getValue().getIsReserved())
               .map(Map.Entry::getKey)
               .collect(Collectors.toList());
       return SeatData.getInstance().getSeatInformation(unreservedSeatIds);
   }

   public List<SeatReservation> getOnHoldSeatInfoByHoldId(Integer seatHoldId) {
      return seatAssignmentMap.entrySet().stream().filter(seatAssignment ->
              seatsOnHoldMap.get(seatHoldId).contains(seatAssignment.getValue().getSeatId()))
              .map(Map.Entry::getValue)
              .collect(Collectors.toList());
   }

   public Long getAvailableSeatCount() {
      return seatAssignmentMap.entrySet().stream().filter(seatAssignment ->
              !seatAssignment.getValue().getIsOnHold() &&
              !seatAssignment.getValue().getIsReserved()).count();
   }
}
