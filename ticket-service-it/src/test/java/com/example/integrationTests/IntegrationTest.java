package com.example.integrationTests;

import com.example.TicketServiceApplication;
import com.example.TicketServiceConfiguration;
import com.example.datatype.SeatHold;
import com.example.datatype.SeatInformation;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class IntegrationTest {
   private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("integration-test.yml");
   private static final String SERVICE_NAME = "/ticketService";
   private static final String AVAILABLE_SEAT_ENDPOINT_URI = "/availableSeatCount";
   private static final String FIND_AND_HOLD_ENDPOINT_URI = "/findAndHoldSeats";
   private static final String RESERVE_SEAT_ENDPOINT_URI = "/findAndReserveSeats";
   private static final String CUSTOMER_EMAIL= "test@email.com";

   @ClassRule
   public static final DropwizardAppRule<TicketServiceConfiguration> RULE = new DropwizardAppRule<>(
           TicketServiceApplication.class, CONFIG_PATH);

   @Test
   public void testGetAvailableSeats() throws Exception {
      final Integer numberOfSeats = RULE.client().target(getAvailableSeatCountUrl())
              .request().get().readEntity(Integer.class);
      assertEquals(81, numberOfSeats.intValue());
   }

   @Test
   public void testHoldAndReserveSeats() throws Exception {
      SeatHold seatHold = getClient(getFindAndHoldUrl())
              .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)
              .queryParam("numberOfSeats", "8")
              .queryParam("email", CUSTOMER_EMAIL)
              .request().put(Entity.entity(null, MediaType.APPLICATION_JSON_TYPE))
              .readEntity(SeatHold.class);
      assertReflectionEquals(getExpectedSeatHoldData(seatHold.getSeatHoldId(), 1, 8),
              seatHold);

      // expect number of available seats to drop by 8
      Integer numberOfSeats = getClient(getAvailableSeatCountUrl())
              .request().get().readEntity(Integer.class);
      assertEquals(73, numberOfSeats.intValue());

      // wait until hold expires, then verify available seats are back to full
      TimeUnit.SECONDS.sleep(RULE.getConfiguration().getMaxOnHoldDuration());
      numberOfSeats = RULE.client().target(getAvailableSeatCountUrl())
              .request().get().readEntity(Integer.class);
      assertEquals(81, numberOfSeats.intValue());

      // request seat holds again, confirm the same seats are available
      seatHold = getClient(getFindAndHoldUrl())
              .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)
              .queryParam("numberOfSeats", "8")
              .queryParam("email", CUSTOMER_EMAIL)
              .request().put(Entity.entity(null, MediaType.APPLICATION_JSON_TYPE))
              .readEntity(SeatHold.class);
      assertReflectionEquals(getExpectedSeatHoldData(seatHold.getSeatHoldId(), 1, 8),
              seatHold);

      String message = getClient(getReserveSeatUrl())
              .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)
              .queryParam("seatHoldId", seatHold.getSeatHoldId())
              .queryParam("email", CUSTOMER_EMAIL)
              .request().put(Entity.entity(null, MediaType.APPLICATION_JSON_TYPE))
              .readEntity(String.class);
      assertEquals("Reservation Complete", message);

      // wait for the hold expiration duration, verify the seats are still reserved
      TimeUnit.SECONDS.sleep(RULE.getConfiguration().getMaxOnHoldDuration());
      numberOfSeats = RULE.client().target(getAvailableSeatCountUrl())
              .request().get().readEntity(Integer.class);
      assertEquals(73, numberOfSeats.intValue());
   }

   private String getAvailableSeatCountUrl() {
      return RULE.getConfiguration().getTicketServiceBaseUrl() + SERVICE_NAME +
              AVAILABLE_SEAT_ENDPOINT_URI;
   }

   private String getFindAndHoldUrl() {
      return RULE.getConfiguration().getTicketServiceBaseUrl() + SERVICE_NAME +
              FIND_AND_HOLD_ENDPOINT_URI;
   }

   private String getReserveSeatUrl() {
      return RULE.getConfiguration().getTicketServiceBaseUrl() + SERVICE_NAME +
              RESERVE_SEAT_ENDPOINT_URI;
   }

   private SeatHold getExpectedSeatHoldData(int seatHoldId, int rowCount, int colCount) {
      int seatIdIndex = 0;
      List<SeatInformation> seatInformationList = new ArrayList<>();
      for (int rowNumber = 0; rowNumber < rowCount; rowNumber++) {
         for (int colNumber = 0; colNumber < colCount; colNumber++) {
            int seatId = seatIdIndex++;
            SeatInformation seatInformation = new SeatInformation();
            seatInformation.setSeatId(seatId);
            seatInformation.setRowNumber(rowNumber);
            seatInformation.setColumnNumber(colNumber);
            seatInformationList.add(seatInformation);
         }
      }
      SeatHold seatHold = new SeatHold();
      seatHold.setSeatHoldId(seatHoldId);
      seatHold.setSeatInformationList(seatInformationList);
      return seatHold;
   }

   private WebTarget getClient(String url) {
      return RULE.client().target(url);
   }
}
