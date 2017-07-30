package com.example.exceptions;

public class NotEnoughSeatsException extends Exception {
   public NotEnoughSeatsException(String message) {
      super(message);
   }
}
