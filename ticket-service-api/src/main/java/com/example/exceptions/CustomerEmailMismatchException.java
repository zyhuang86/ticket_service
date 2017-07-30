package com.example.exceptions;

public class CustomerEmailMismatchException extends Exception {
   public CustomerEmailMismatchException() {
      super("Customer E-mail does not match the E-mail recorded under the seat hold id");
   }
}
