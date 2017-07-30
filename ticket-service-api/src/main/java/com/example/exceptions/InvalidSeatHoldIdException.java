package com.example.exceptions;

public class InvalidSeatHoldIdException extends Exception {
   public InvalidSeatHoldIdException() {
      super("Seat hold is either invalid or has expired");
   }
}
