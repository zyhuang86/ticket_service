package com.example.exceptions;

public class NotEnoughSeatsException extends Exception {
   public NotEnoughSeatsException() {
      super("Number of seats to put on hold exceeds available seat count");
   }
}
