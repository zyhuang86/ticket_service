package com.example;

import io.dropwizard.Configuration;

import javax.validation.Valid;

public class TicketServiceConfiguration extends Configuration {

   @Valid
   private Integer maxOnHoldDuration;

   @Valid
   private String ticketServiceBaseUrl;

   public Integer getMaxOnHoldDuration() {
      return maxOnHoldDuration;
   }

   public void setMaxOnHoldDuration(Integer maxOnHoldDuration) {
      this.maxOnHoldDuration = maxOnHoldDuration;
   }

   public String getTicketServiceBaseUrl() {
      return ticketServiceBaseUrl;
   }

   public void setTicketServiceBaseUrl(String ticketServiceBaseUrl) {
      this.ticketServiceBaseUrl = ticketServiceBaseUrl;
   }

}
