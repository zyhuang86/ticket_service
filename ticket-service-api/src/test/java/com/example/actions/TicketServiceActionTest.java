package com.example.actions;

import com.example.dataAccess.SeatDataAccess;
import com.example.datatype.SeatHold;
import com.example.datatype.SeatInformation;
import com.example.exceptions.NotEnoughSeatsException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class TicketServiceActionTest {
   private final static Integer ROW_COUNT = 3;
   private final static Integer COLUMN_COUNT = 3;
   private final static String CUSTOMER_EMAIL = "test@email.com";
   private SeatDataAccess seatDataAccessMock = mock(SeatDataAccess.class);
   private ExecutorServiceAction executorServiceActionMock = mock(ExecutorServiceAction.class);
   private List<SeatInformation> seatInformationList = new ArrayList<>();

   private TicketServiceAction ticketServiceAction = new TicketServiceAction(seatDataAccessMock,
           executorServiceActionMock);

   @Before
   public void init() {
   }

   @Test
   public void ticketServiceAction_testGetNumberOfSeatsAvailable_expectInteger() {
      resetSeatInfoList();
      when(seatDataAccessMock.getAvailableSeatCount()).thenReturn(Long.valueOf(seatInformationList.size()));
      assertEquals(seatInformationList.size(), ticketServiceAction.numSeatsAvailable());
   }

   @Test
   public void ticketServiceAction_testFindAndHoldSeatsSuccess_expectValidSeatHold()
           throws Exception {
      resetSeatInfoList();

      // all seats available, reserve 2, expect to put first 2 seats in the first row on hold
      final List<Integer> expectedFirstRowSeatIdsToHold = Arrays.asList(0, 1);
      testAndVerifyFindAndHoldSeat(expectedFirstRowSeatIdsToHold, 2);

      // 2 seats taken, reserve 2, expect to put first 2 seats in the second row on hold
      seatInformationList = new ArrayList<>(seatInformationList.stream().filter(seatInfo ->
              !expectedFirstRowSeatIdsToHold.contains(seatInfo.getSeatId()))
              .collect(Collectors.toList()));
      when(seatDataAccessMock.getUnreservedSeats()).thenReturn(seatInformationList);
      final List<Integer> expectedSecondRowSeatIdsToHold = Arrays.asList(3, 4);
      testAndVerifyFindAndHoldSeat(expectedSecondRowSeatIdsToHold, 2);

      // 4 seats taken, reserve 4, expect to put third seat in the first row
      // and entire third row on hold
      seatInformationList = new ArrayList<>(seatInformationList.stream().filter(seatInfo ->
              !expectedSecondRowSeatIdsToHold.contains(seatInfo.getSeatId()))
              .collect(Collectors.toList()));
      when(seatDataAccessMock.getUnreservedSeats()).thenReturn(seatInformationList);
      final List<Integer> expectedSegmentedRowSeatIdsToHold = Arrays.asList(6, 7, 8, 2);
      testAndVerifyFindAndHoldSeat(expectedSegmentedRowSeatIdsToHold, 4);

      // last seat, expect third seats of second row on hold
      seatInformationList = new ArrayList<>(seatInformationList.stream().filter(seatInfo ->
              !expectedSegmentedRowSeatIdsToHold.contains(seatInfo.getSeatId()))
              .collect(Collectors.toList()));
      when(seatDataAccessMock.getUnreservedSeats()).thenReturn(seatInformationList);
      final List<Integer> last2SeatIds = Arrays.asList(5);
      testAndVerifyFindAndHoldSeat(last2SeatIds, 1);
   }

   @Test(expected = NotEnoughSeatsException.class)
   public void ticketServiceAction_testFindAndHoldSeatsNotEnoughSeats_expectException() throws Exception {
      resetSeatInfoList();
      ticketServiceAction.findAndHoldSeats(seatInformationList.size() + 1, CUSTOMER_EMAIL);
   }

   private void testAndVerifyFindAndHoldSeat(List<Integer> expectedSeatIdsToHold,
                                             Integer numSeats) throws Exception {
      SeatHold expectedSeatHold = createSeatHold(1, expectedSeatIdsToHold);
      when(seatDataAccessMock.holdSeats(eq(expectedSeatIdsToHold), eq(CUSTOMER_EMAIL)))
              .thenReturn(expectedSeatHold);
      when(seatDataAccessMock.getAvailableSeatCount()).thenReturn(Long.valueOf(seatInformationList.size()));

      SeatHold receivedSeatHoldId = ticketServiceAction.findAndHoldSeats(numSeats, CUSTOMER_EMAIL);
      assertReflectionEquals(createSeatHold(receivedSeatHoldId.getSeatHoldId(), expectedSeatIdsToHold),
              receivedSeatHoldId);
   }

   private List<SeatInformation> generateSeatInformation() {
      int seatIdIndex = 0;
      for (int rowNumber = 0; rowNumber < ROW_COUNT; rowNumber++) {
         for (int colNumber = 0; colNumber < COLUMN_COUNT; colNumber++) {
            int seatId = seatIdIndex++;
            SeatInformation seatInformation = new SeatInformation();
            seatInformation.setSeatId(seatId);
            seatInformation.setRowNumber(rowNumber);
            seatInformation.setColumnNumber(colNumber);
            seatInformationList.add(seatInformation);
         }
      }
      return seatInformationList;
   }

   private SeatHold createSeatHold(Integer seatHoldId, List<Integer> seatIdList) {
      SeatHold seatHold = new SeatHold();
      seatHold.setSeatHoldId(seatHoldId);
      seatHold.setSeatInformationList(seatInformationList.stream()
              .filter(seatInfo -> seatIdList.contains(seatInfo.getSeatId()))
              .collect(Collectors.toList()));
      return seatHold;
   }

   private void resetSeatInfoList() {
      generateSeatInformation();
      when(seatDataAccessMock.getUnreservedSeats()).thenReturn(seatInformationList);
   }
}
