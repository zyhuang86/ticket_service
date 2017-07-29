package com.example.seatData;

import com.example.datatype.SeatInformation;
import com.example.datatype.SeatReservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SeatData {
   private static SeatData seatDataInstance;
   private final static Integer MAX_ROW_COUNT = 3;
   private final static Integer MAX_COLUMN_COUNT = 3;
   private Integer seatHoldIdIndex = 0;
   private Map<Integer, SeatInformation> seatInformationMap = new HashMap<>();
   private Map<Integer, SeatReservation> seatAssignmentMap = new ConcurrentHashMap<>();
   private Map<Integer, List<Integer>> seatsOnHoldMap = new ConcurrentHashMap<>();

   private SeatData() {
      int seatIdIndex = 0;
      for (int rowNumber = 0; rowNumber < MAX_ROW_COUNT; rowNumber++) {
         for (int colNumber = 0; colNumber < MAX_COLUMN_COUNT; colNumber++) {
            int seatId = seatIdIndex++;
            SeatInformation seatInformation = new SeatInformation();
            seatInformation.setSeatId(seatId);
            seatInformation.setRowNumber(rowNumber);
            seatInformation.setColumnNumber(colNumber);
            seatInformationMap.put(seatId, seatInformation);

            SeatReservation seatReservation = new SeatReservation();
            seatReservation.setSeatId(seatId);
            seatReservation.setCustomerEmail("");
            seatReservation.setIsOnHold(false);
            seatReservation.setIsReserved(false);
            seatAssignmentMap.put(seatId, seatReservation);
         }
      }
   }

   public static SeatData getInstance() {
      if (null == seatDataInstance) {
         seatDataInstance = new SeatData();
      }
      return seatDataInstance;
   }

   public List<SeatInformation> getSeatInformation(List<Integer> seatIds) {
      List<SeatInformation> seatInformationList = new ArrayList<>();
      seatIds.forEach(seatId -> seatInformationList.add(seatInformationMap.get(seatId)));
      return seatInformationList;
   }

   public Map<Integer, SeatReservation> getSeatAssignmentMap() {
      return seatAssignmentMap;
   }

   public Map<Integer, List<Integer>> getSeatsOnHoldMap() {
      return seatsOnHoldMap;
   }

   public Integer getNextSeatHoldIdIndex() {
      return seatHoldIdIndex++;
   }
}
