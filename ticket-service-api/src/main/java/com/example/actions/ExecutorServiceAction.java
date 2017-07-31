package com.example.actions;

import com.example.dataAccess.SeatDataAccess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceAction {
   private Integer maxOnHoldDuration;
   private SeatDataAccess seatDataAccess;
   private Map<Integer, ScheduledFuture> scheduledTaskMap = new ConcurrentHashMap<>();

   public ExecutorServiceAction(SeatDataAccess seatDataAccess,
                                Integer maxOnHoldDuration) {
      this.seatDataAccess = seatDataAccess;
      this.maxOnHoldDuration = maxOnHoldDuration;
   }

   void scheduleOnHoldCleanUpTask(Integer seatHoldId) {
      ScheduledExecutorService scheduledExecutorService =
              Executors.newScheduledThreadPool(1);
      ScheduledFuture scheduledFuture = scheduledExecutorService.schedule(() -> {
                      seatDataAccess.unholdSeats(seatHoldId);
                      scheduledTaskMap.remove(seatHoldId); },
                      maxOnHoldDuration,
                      TimeUnit.SECONDS);
      scheduledTaskMap.putIfAbsent(seatHoldId, scheduledFuture);
      scheduledExecutorService.shutdown();
   }

   void cancelScheduledOnHoldCleanUpTask(Integer seatHoldId) {
      ScheduledFuture scheduledFuture = scheduledTaskMap.get(seatHoldId);
      if (scheduledFuture != null && !scheduledFuture.isDone()) {
         scheduledFuture.cancel(true);
      }
      scheduledTaskMap.remove(seatHoldId);
   }
}
