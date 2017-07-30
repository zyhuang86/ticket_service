package com.example;

import com.example.actions.ExecutorServiceAction;
import com.example.actions.TicketServiceAction;
import com.example.dataAccess.SeatDataAccess;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.example.services.TicketService;

import java.util.logging.Logger;

public class TicketServiceApplication extends Application<TicketServiceConfiguration> {
   private final static Logger LOG = Logger.getLogger(SeatDataAccess.class.getName());

   public static void main(String[] args) throws Exception {
      new TicketServiceApplication().run(args);
   }

   @Override
   public String getName() {
      return "ticket-service";
   }

   @Override
   public void initialize(Bootstrap<TicketServiceConfiguration> bootstrap) {
   }

   @Override
   public void run(TicketServiceConfiguration configuration,
                   Environment environment) {
      SeatDataAccess seatDataAccess = new SeatDataAccess();
      ExecutorServiceAction executorServiceAction = new ExecutorServiceAction(seatDataAccess,
              configuration.getMaxOnHoldDuration());
      TicketServiceAction ticketServiceAction = new TicketServiceAction(seatDataAccess,
              executorServiceAction);

      // register resource now
      environment.jersey().register(new TicketService(ticketServiceAction));
      LOG.info("Service Started");
   }
}
