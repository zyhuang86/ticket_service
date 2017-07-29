package com.example.datatype;

import java.util.List;

public class SeatHold {
   private int seatHoldId;

   private List<SeatInformation> seatInformationList;

   public int getSeatHoldId() {
      return seatHoldId;
   }

   public void setSeatHoldId(int seatHoldId) {
      this.seatHoldId = seatHoldId;
   }

   public List<SeatInformation> getSeatInformationList() {
      return seatInformationList;
   }

   public void setSeatInformationList(List<SeatInformation> seatInformationList) {
      this.seatInformationList = seatInformationList;
   }
}
