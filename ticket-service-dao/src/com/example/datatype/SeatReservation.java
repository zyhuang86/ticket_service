package com.example.datatype;

import java.util.concurrent.atomic.AtomicBoolean;

public class SeatReservation {
   private int seatId;
   private AtomicBoolean isOnHold = new AtomicBoolean(false);
   private AtomicBoolean isReserved = new AtomicBoolean(false);
   private volatile String customerEmail;

   public int getSeatId() {
      return seatId;
   }

   public void setSeatId(int seatId) {
      this.seatId = seatId;
   }

   public Boolean getIsOnHold() {
      return isOnHold.get();
   }

   public void setIsOnHold(Boolean onHold) {
      isOnHold.compareAndSet(!onHold, onHold);
   }

   public Boolean getIsReserved() {
      return isReserved.get();
   }

   public void setIsReserved(Boolean reserved) {
      isOnHold.compareAndSet(!reserved, reserved);
   }

   public String getCustomerEmail() {
      return customerEmail;
   }

   public void setCustomerEmail(String customerEmail) {
      this.customerEmail = customerEmail;
   }
}
