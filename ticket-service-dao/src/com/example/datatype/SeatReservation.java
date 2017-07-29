package com.example.datatype;

public class SeatReservation {
   private int seatId;
   private Boolean isOnHold;
   private Boolean isReserved;
   private String customerEmail;

   public int getSeatId() {
      return seatId;
   }

   public void setSeatId(int seatId) {
      this.seatId = seatId;
   }

   public Boolean getIsOnHold() {
      return isOnHold;
   }

   public void setIsOnHold(Boolean onHold) {
      isOnHold = onHold;
   }

   public Boolean getIsReserved() {
      return isReserved;
   }

   public void setIsReserved(Boolean reserved) {
      isReserved = reserved;
   }

   public String getCustomerEmail() {
      return customerEmail;
   }

   public void setCustomerEmail(String customerEmail) {
      this.customerEmail = customerEmail;
   }
}
