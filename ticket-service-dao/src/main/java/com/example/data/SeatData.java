package com.example.data;

import com.example.datatype.SeatInformation;
import com.example.datatype.SeatReservation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SeatData {
   private final static Logger LOG = Logger.getLogger(SeatData.class.getName());

   private static SeatData seatDataInstance;
   private final static Integer ROW_COUNT = 9;
   private final static Integer COLUMN_COUNT = 9;
   private Integer seatHoldIdIndex = 0;
   private Map<Integer, SeatInformation> seatInformationMap = new HashMap<>();
   private Map<Integer, SeatReservation> seatAssignmentMap = new ConcurrentHashMap<>();
   private Map<Integer, List<Integer>> seatsOnHoldMap = new ConcurrentHashMap<>();

   private SeatData() {
      int seatIdIndex = 0;
      for (int rowNumber = 0; rowNumber < ROW_COUNT; rowNumber++) {
         for (int colNumber = 0; colNumber < COLUMN_COUNT; colNumber++) {
            int seatId = seatIdIndex++;
            SeatInformation seatInformation = new SeatInformation();
            seatInformation.setSeatId(seatId);
            seatInformation.setRowNumber(rowNumber);
            seatInformation.setColumnNumber(colNumber);
            seatInformationMap.putIfAbsent(seatId, seatInformation);

            SeatReservation seatReservation = new SeatReservation();
            seatReservation.setSeatId(seatId);
            seatReservation.setCustomerEmail("");
            seatReservation.setIsOnHold(false);
            seatReservation.setIsReserved(false);
            seatAssignmentMap.putIfAbsent(seatId, seatReservation);
         }
      }
      LOG.info("Initial Seating Information populated");
   }

   public static SeatData getInstance() {
      if (null == seatDataInstance) {
         seatDataInstance = new SeatData();
      }
      return seatDataInstance;
   }

   public List<SeatInformation> getSeatInformation(List<Integer> seatIds) {
      List<SeatInformation> seatInformationList = Collections.synchronizedList(new ArrayList<>());
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
